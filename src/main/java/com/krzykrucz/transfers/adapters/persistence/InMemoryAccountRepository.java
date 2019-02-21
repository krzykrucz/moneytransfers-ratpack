package com.krzykrucz.transfers.adapters.persistence;

import com.google.common.collect.Maps;
import com.krzykrucz.transfers.domain.account.Account;
import com.krzykrucz.transfers.domain.account.AccountNumber;
import com.krzykrucz.transfers.domain.account.AccountRepository;
import com.krzykrucz.transfers.domain.account.TransferReferenceNumber;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static com.krzykrucz.transfers.domain.common.DomainException.checkDomainState;

@Singleton
public class InMemoryAccountRepository implements AccountRepository {

    protected final Map<AccountNumber, Account> accountsByNumber = Maps.newConcurrentMap();
    protected final Map<TransferReferenceNumber, Account> accountsByTransfer = Maps.newConcurrentMap();

    @Override
    public void save(Account account) {
        checkConcurrentModification(account);

        account.finishModification();

        accountsByNumber.put(account.getAccountNumber(), account);
        account.getPendingOutcomingTransfers()
                .forEach(transferRefNumber -> accountsByTransfer.put(transferRefNumber, account));

//        events.forEach(eventPublisher::publish); // TODO remove
    }

    private void checkConcurrentModification(Account account) {
        final AccountNumber number = account.getAccountNumber();
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
