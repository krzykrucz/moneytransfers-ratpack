package com.krzykrucz.transfers.infrastructure.persistence

import com.google.common.collect.Queues
import com.google.common.collect.Sets
import com.krzykrucz.transfers.domain.account.Account
import com.krzykrucz.transfers.domain.account.AccountNumber
import com.krzykrucz.transfers.domain.account.AccountRepository
import com.krzykrucz.transfers.domain.account.TransferReferenceNumber
import com.krzykrucz.transfers.domain.common.DomainEventPublisher
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton
import java.util.function.Predicate

@Singleton
@Slf4j
class InMemoryThrowingAccountRepository implements AccountRepository {

    private final DomainEventPublisher eventPublisher

    private final Set<Account> accounts = Sets.newConcurrentHashSet()

    private final Queue<ThrowableException> exceptionsToThrowOnSave = Queues.newConcurrentLinkedQueue()

    private class ThrowableException {
        private final Exception exception
        private final Predicate<Account> predicate

        ThrowableException(Exception exception, Predicate<Account> when) {
            this.exception = exception
            this.predicate = when
        }

        boolean isThrowableFor(Account account) {
            predicate.test(account)
        }

        void throwIt() {
            throw exception
        }
    }

    private int saveInvocations = 0

    @Inject
    InMemoryThrowingAccountRepository(DomainEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher
    }

    @Override
    synchronized void save(Account account) {
        saveInvocations++
        def throwable = exceptionsToThrowOnSave.peek()
        if (throwable != null && throwable.isThrowableFor(account)) {
            exceptionsToThrowOnSave.poll().throwIt()
        }
        def events = account.finishModification()
        accounts.add(account)
        events.forEach { eventPublisher.publish(it) }
    }

    void throwExceptionsOnSave(Exception exception, int numberOfExceptions = 1, Predicate<Account> when = { true }) {
        (1..numberOfExceptions).forEach {
            exceptionsToThrowOnSave.add(new ThrowableException(exception, when))
        }
    }

    void clear() {
        accounts.clear()
    }

    boolean timesSaveShouldBeInvoked(int number) {
        def matches = saveInvocations == number
        if (!matches) log.error("save() invoked ${saveInvocations} times")
        matches
    }


    @Override
    Account findByTransfer(TransferReferenceNumber transferReferenceNumber) {
        def account = accounts.find { it.pendingOutcomingTransfers.contains(transferReferenceNumber) }
        account.copy()
    }

    @Override
    Account findByAccountNumber(AccountNumber accountNumber) {
        def account = accounts.find { it.accountNumber == accountNumber }
        account.copy()
    }

}
