package com.krzykrucz.transfers.application;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.krzykrucz.transfers.application.api.command.*;
import com.krzykrucz.transfers.domain.CurrencyExchanger;
import com.krzykrucz.transfers.domain.account.*;
import io.github.resilience4j.ratpack.retry.Retry;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

@Singleton
public class TransfersApplicationServiceImpl implements TransfersApplicationService {

    private final AccountRepository accountRepository;

    private final CurrencyExchanger currencyExchanger;

    @Inject
    public TransfersApplicationServiceImpl(AccountRepository accountRepository, CurrencyExchanger externalCurrencyExchanger) {
        this.accountRepository = accountRepository;
        this.currencyExchanger = externalCurrencyExchanger;
    }

    @Override
    @Retry(name = "retryExceptions")
    public void transfer(PerformMoneyTransferCommand moneyTransferCommand) {
        final Account account = accountRepository.findByAccountNumber(moneyTransferCommand.getFrom());
        account.commissionTransferTo(moneyTransferCommand.getTo(), moneyTransferCommand.getValue());
        accountRepository.save(account);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void openAccount(OpenAccountCommand openAccountCommand) {
        final Account newAccount = new Account(
                AccountIdentifier.generate(),
                openAccountCommand.getAccountNumber(),
                openAccountCommand.getCurrency()
        );
        // TODO use saveIfAbsent for idempotency
        accountRepository.save(newAccount);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void depositMoney(DepositMoneyCommand depositMoneyCommand) {
        final Account account = accountRepository.findByAccountNumber(depositMoneyCommand.getAccountNumber());
        final CurrencyUnit accountCurrency = account.getCurrency();
        final Money moneyToDeposit = depositMoneyCommand.getValue();
        final Money exchangedMoney = currencyExchanger.exchangeIfNecessary(moneyToDeposit, accountCurrency);

        account.depositMoney(exchangedMoney);

        accountRepository.save(account);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void acceptTransfer(AcceptTransferCommand command) {
        final TransferReferenceNumber transferReferenceNumber = command.getTransferReferenceNumber();
        final Account account = accountRepository.findByTransfer(transferReferenceNumber);

        account.confirmTransfer(transferReferenceNumber);

        accountRepository.save(account);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void rejectTransfer(RejectTransferCommand command) {
        final TransferReferenceNumber transferReferenceNumber = command.getTransferReferenceNumber();
        final Account account = accountRepository.findByTransfer(transferReferenceNumber);

        account.rejectTransfer(transferReferenceNumber);

        accountRepository.save(account);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void receiveTransfer(ReceiveTransferCommand command) {
        final MoneyTransfer moneyTransfer = new MoneyTransfer(
                command.getTransferReferenceNumber(),
                command.getValue(),
                command.getFrom(),
                command.getTo()
        );
        final Account account = accountRepository.findByAccountNumber(moneyTransfer.getTo());
        final CurrencyUnit accountCurrency = account.getCurrency();
        final MoneyTransfer moneyTransferWithExchangedValue =
                currencyExchanger.exchangeIfNecessary(moneyTransfer, accountCurrency);

        account.receiveTransfer(moneyTransferWithExchangedValue);

        accountRepository.save(account);
    }
}
