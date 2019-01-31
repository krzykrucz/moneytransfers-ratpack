package com.krzykrucz.transfers.domain.account;

public interface AccountRepository {

    void save(Account account);

    Account findByTransfer(TransferReferenceNumber transferReferenceNumber);

    Account findByAccountNumber(AccountNumber accountNumber);

}
