package com.krzykrucz.transfers.application;


import com.krzykrucz.transfers.application.api.command.DepositMoneyCommand;
import com.krzykrucz.transfers.application.api.command.MoneyTransferCommand;
import com.krzykrucz.transfers.application.api.command.OpenAccountCommand;
import com.krzykrucz.transfers.domain.CurrencyExchanger;
import com.krzykrucz.transfers.domain.account.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransfersApplicationService {

    private final AccountRepository accountRepository;

    private final CurrencyExchanger currencyExchanger;

    @Inject
    public TransfersApplicationService(AccountRepository accountRepository, CurrencyExchanger externalCurrencyExchanger) {
        this.accountRepository = accountRepository;
        this.currencyExchanger = externalCurrencyExchanger;
    }

    public void transfer(MoneyTransferCommand moneyTransferCommand) {
        final Account account = accountRepository.findByAccountNumber(moneyTransferCommand.getFrom());
        account.commissionTransferTo(moneyTransferCommand.getTo(), moneyTransferCommand.getValue());
        accountRepository.save(account);
    }

    public void openAccount(OpenAccountCommand openAccountCommand) {
        final Account newAccount = new Account(
                AccountIdentifier.generate(),
                openAccountCommand.getAccountNumber(),
                openAccountCommand.getCurrency(),
                currencyExchanger
        );
        // TODO use saveIfAbsent for idempotency
        accountRepository.save(newAccount);
    }

    public void depositMoney(DepositMoneyCommand depositMoneyCommand) {
        final Account account = accountRepository.findByAccountNumber(depositMoneyCommand.getAccountNumber());
        account.depositMoney(depositMoneyCommand.getValue());
        accountRepository.save(account);
    }

    public void acceptTransfer(TransferReferenceNumber transferReferenceNumber) {
        final Account account = accountRepository.findByTransfer(transferReferenceNumber);
        account.confirmTransfer(transferReferenceNumber);
        accountRepository.save(account);
    }

    public void rejectTransfer(TransferReferenceNumber transferReferenceNumber) {
        final Account account = accountRepository.findByTransfer(transferReferenceNumber);
        account.rejectTransfer(transferReferenceNumber);
        accountRepository.save(account);
    }

    public void receiveTransfer(MoneyTransfer moneyTransfer) {
        final Account account = accountRepository.findByAccountNumber(moneyTransfer.getTo());
        account.receiveTransfer(moneyTransfer);
        accountRepository.save(account);
    }
}
