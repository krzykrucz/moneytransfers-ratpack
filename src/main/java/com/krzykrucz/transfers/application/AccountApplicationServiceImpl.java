package com.krzykrucz.transfers.application;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.krzykrucz.transfers.application.api.command.*;
import com.krzykrucz.transfers.application.events.internal.MoneyTransferAcceptedHandler;
import com.krzykrucz.transfers.application.events.internal.MoneyTransferCommissionedHandler;
import com.krzykrucz.transfers.application.events.internal.MoneyTransferRejectedHandler;
import com.krzykrucz.transfers.domain.CurrencyExchanger;
import com.krzykrucz.transfers.domain.account.*;
import com.krzykrucz.transfers.domain.common.DomainEvent;
import com.krzykrucz.transfers.domain.common.DomainEventPublisher;
import io.vavr.collection.List;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

@Singleton
public class AccountApplicationServiceImpl implements AccountApplicationService {

    private final AccountRepository accountRepository;

    private final CurrencyExchanger currencyExchanger;

    private final DomainEventPublisher eventPublisher;

    @Inject
    public AccountApplicationServiceImpl(AccountRepository accountRepository,
                                         CurrencyExchanger externalCurrencyExchanger,
                                         DomainEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.currencyExchanger = externalCurrencyExchanger;
        this.eventPublisher = eventPublisher;

        eventPublisher.subcribe(new MoneyTransferAcceptedHandler(this));
        eventPublisher.subcribe(new MoneyTransferRejectedHandler(this));
        eventPublisher.subcribe(new MoneyTransferCommissionedHandler(this));
    }

    @Override
    public void transfer(PerformMoneyTransferCommand moneyTransferCommand) {
        final Account account = accountRepository.findByAccountNumber(moneyTransferCommand.getFrom());

        account.commissionTransferTo(moneyTransferCommand.getTo(), moneyTransferCommand.getValue());

        saveAccountAndPublishEvents(account);
    }

    private void saveAccountAndPublishEvents(Account account) {
        final List<DomainEvent> domainEvents = account.getDomainEvents();
        accountRepository.save(account);
        domainEvents.forEach(eventPublisher::publish);
    }

    @Override
    public void openAccount(OpenAccountCommand openAccountCommand) {
        final Account newAccount = new Account(
                AccountIdentifier.generate(),
                openAccountCommand.getAccountNumber(),
                openAccountCommand.getCurrency()
        );
        // TODO use saveIfAbsent for idempotency
        saveAccountAndPublishEvents(newAccount);
    }

    @Override
    public void depositMoney(DepositMoneyCommand depositMoneyCommand) {
        final Account account = accountRepository.findByAccountNumber(depositMoneyCommand.getAccountNumber());
        final CurrencyUnit accountCurrency = account.getCurrency();
        final Money moneyToDeposit = depositMoneyCommand.getValue();
        final Money exchangedMoney = currencyExchanger.exchangeIfNecessary(moneyToDeposit, accountCurrency);

        account.depositMoney(exchangedMoney);

        saveAccountAndPublishEvents(account);
    }

    @Override
    public void acceptTransfer(AcceptTransferCommand command) {
        final TransferReferenceNumber transferReferenceNumber = command.getTransferReferenceNumber();
        final Account account = accountRepository.findByTransfer(transferReferenceNumber);

        account.confirmTransfer(transferReferenceNumber);

        saveAccountAndPublishEvents(account);
    }

    @Override
    public void rejectTransfer(RejectTransferCommand command) {
        final TransferReferenceNumber transferReferenceNumber = command.getTransferReferenceNumber();
        final Account account = accountRepository.findByTransfer(transferReferenceNumber);

        account.rejectTransfer(transferReferenceNumber);

        saveAccountAndPublishEvents(account);
    }

    @Override
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

        saveAccountAndPublishEvents(account);
    }
}
