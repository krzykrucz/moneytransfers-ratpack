package com.krzykrucz.transfers.domain.account;

public interface AccountRepository {

    void save(Account account);

    Account findOne(AccountIdentifier accountIdentifier);

    Account findByTransfer(TransferReferenceNumber transferReferenceNumber);

    Account findByAccountNumber(AccountNumber accountNumber);

}
