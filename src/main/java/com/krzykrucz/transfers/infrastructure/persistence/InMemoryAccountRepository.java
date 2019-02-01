package com.krzykrucz.transfers.infrastructure.persistence;

import com.google.common.collect.Maps;
import com.krzykrucz.transfers.domain.account.Account;
import com.krzykrucz.transfers.domain.account.AccountNumber;
import com.krzykrucz.transfers.domain.account.AccountRepository;
import com.krzykrucz.transfers.domain.account.TransferReferenceNumber;
import com.krzykrucz.transfers.domain.common.DomainEvent;
import com.krzykrucz.transfers.domain.common.DomainEventPublisher;
import io.vavr.collection.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static com.krzykrucz.transfers.domain.common.DomainException.checkDomainState;

@Singleton
public class InMemoryAccountRepository implements AccountRepository {

    private final DomainEventPublisher eventPublisher;

    private final Map<AccountNumber, Account> accountsByNumber = Maps.newConcurrentMap();
    private final Map<TransferReferenceNumber, Account> accountsByTransfer = Maps.newConcurrentMap();

    @Inject
    public InMemoryAccountRepository(DomainEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void save(Account account) {
        checkConcurrentModification(account);

        final List<DomainEvent> events = account.finishModification();

        accountsByNumber.put(account.getNumber(), account);
        account.getPendingTransfers()
                .forEach(transferRefNumber -> accountsByTransfer.put(transferRefNumber, account));

        events.forEach(eventPublisher::publish);
    }

    private void checkConcurrentModification(Account account) {
        final AccountNumber number = account.getNumber();
        final Predicate<Account> accountsDifferentVersions =
                persistedAccount -> !persistedAccount.hasSameVersionAs(account.getVersion());
        Optional.ofNullable(accountsByNumber.get(number))
                .filter(accountsDifferentVersions)
                .ifPresent(persistedAccount -> {
                    throw new OptimisticLockException();
                });
    }

    @Override
    public Account findByTransfer(TransferReferenceNumber transferReferenceNumber) {
        checkDomainState(accountsByTransfer.containsKey(transferReferenceNumber), "Account not found");

        final Account account = accountsByTransfer.get(transferReferenceNumber);
        return account.copy();
    }

    @Override
    public Account findByAccountNumber(AccountNumber accountNumber) {
        checkDomainState(accountsByNumber.containsKey(accountNumber), "Account not found");

        final Account account = accountsByNumber.get(accountNumber);
        return account.copy();
    }

}
