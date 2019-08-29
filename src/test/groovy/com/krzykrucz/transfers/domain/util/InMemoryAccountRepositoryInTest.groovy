package com.krzykrucz.transfers.domain.util

import com.krzykrucz.transfers.adapters.persistence.InMemoryAccountRepository
import com.krzykrucz.transfers.domain.account.AccountRepository

class InMemoryAccountRepositoryInTest implements AccountRepository {

    @Delegate
    private AccountRepository accountRepository = new InMemoryAccountRepository();
//    Set<Account> accounts = (Set) []
//
//    def clear() {
//        accounts.clear()
//    }
//
//    @Override
//    void save(Account account) {
//        account.finishModification()
//        accounts.add(account)
//    }
//
//    @Override
//    Account findByTransfer(TransferReferenceNumber transferReferenceNumber) {
//        accounts.find { it.pendingOutcomingTransfers.contains(transferReferenceNumber) }
//    }
//
//    @Override
//    Account findByAccountNumber(AccountNumber accountNumber) {
//        accounts.find { it.accountNumber == accountNumber }
//    }


    def clear() {
        accountRepository = new InMemoryAccountRepository()
    }
}
