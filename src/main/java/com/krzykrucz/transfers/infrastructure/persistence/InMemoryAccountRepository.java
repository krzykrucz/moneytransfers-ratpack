package com.krzykrucz.transfers.infrastructure.persistence;

import com.google.common.collect.Maps;
import com.krzykrucz.transfers.domain.account.*;
import com.krzykrucz.transfers.domain.common.DomainEvent;
import com.krzykrucz.transfers.domain.common.DomainEventPublisher;
import io.vavr.collection.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

@Singleton
// TODO implement optimistic locks
public class InMemoryAccountRepository implements AccountRepository {

    private final DomainEventPublisher eventPublisher;

    private final Map<AccountIdentifier, Account> accounts = Maps.newConcurrentMap();
    private final Map<AccountNumber, Account> accountsByNumber = Maps.newConcurrentMap();
    private final Map<TransferReferenceNumber, Account> accountsByTransfer = Maps.newConcurrentMap();

    @Inject
    public InMemoryAccountRepository(DomainEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void save(Account account) {
        final List<DomainEvent> events = account.getEventsAndFlush();
        accounts.put(account.getId(), account);
        accountsByNumber.put(account.getNumber(), account);
        account.getPendingTransfers()
                .forEach(transferRefNumber -> accountsByTransfer.put(transferRefNumber, account));
        events.forEach(eventPublisher::publish);
    }

    @Override
    public Account findOne(AccountIdentifier accountIdentifier) {
        checkState(accounts.containsKey(accountIdentifier), "Account not found");
        return accounts.get(accountIdentifier);
    }

    @Override
    public Account findByTransfer(TransferReferenceNumber transferReferenceNumber) {
        checkState(accountsByTransfer.containsKey(transferReferenceNumber), "Account not found");
        return accountsByTransfer.get(transferReferenceNumber);
    }

    @Override
    public Account findByAccountNumber(AccountNumber accountNumber) {
        checkState(accountsByNumber.containsKey(accountNumber), "Account not found");
        return accountsByNumber.get(accountNumber);
    }
}
