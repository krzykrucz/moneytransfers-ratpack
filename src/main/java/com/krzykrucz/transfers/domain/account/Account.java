package com.krzykrucz.transfers.domain.account;

import com.krzykrucz.transfers.domain.CurrencyExchanger;
import com.krzykrucz.transfers.domain.DomainEvent;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.Getter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class Account {

    private static final int DEFAULT_TRANSFER_RECEIVE_LIMIT = 10000;

    @Getter
    private final AccountIdentifier id;
    @Getter
    private final AccountNumber number;
    private final CurrencyExchanger currencyExchanger;
    @Getter
    private Money balance;
    private CurrencyUnit currency;
    private Map<TransferReferenceNumber, Money> moneyBlockedOnTransfers;
    private List<DomainEvent> domainEvents;
    private final Money transferReceiveLimit;

    public Account(AccountIdentifier id, AccountNumber number, CurrencyUnit currency, CurrencyExchanger currencyExchanger) {
        this.id = id;
        this.balance = Money.zero(currency);
        this.currency = currency;
        this.number = number;
        this.currencyExchanger = currencyExchanger;
        this.moneyBlockedOnTransfers = HashMap.empty();
        this.domainEvents = List.empty();
        this.transferReceiveLimit = Money.of(currency, DEFAULT_TRANSFER_RECEIVE_LIMIT);
    }

    public void commissionTransferTo(AccountNumber anotherAccount, Money transferValue) {
        final Money transferValueInAccountsCurrency = exchangeIfNecessary(transferValue);
        checkState(!balance.isLessThan(transferValueInAccountsCurrency), "Not enough money on account to do a transfer");

        this.balance = balance.minus(transferValueInAccountsCurrency);

        final MoneyTransfer transfer = MoneyTransfer.generateMoneyTransfer(transferValue, this.number, anotherAccount);
        this.moneyBlockedOnTransfers = moneyBlockedOnTransfers.put(transfer.getReferenceNumber(), transferValueInAccountsCurrency);

        this.domainEvents = domainEvents.push(new MoneyTransferCommissioned(transferValueInAccountsCurrency, transfer));
    }

    public void confirmTransfer(TransferReferenceNumber transferReferenceNumber) {
        this.moneyBlockedOnTransfers = moneyBlockedOnTransfers.remove(transferReferenceNumber);
    }

    public void rejectTransfer(TransferReferenceNumber transferReferenceNumber) {
        this.moneyBlockedOnTransfers.get(transferReferenceNumber)
                .peek(moneyToAddBack -> this.balance = balance.plus(moneyToAddBack));
        this.moneyBlockedOnTransfers = moneyBlockedOnTransfers.remove(transferReferenceNumber);
    }

    public void receiveTransfer(MoneyTransfer transfer) {
        checkState(transfer.getTo().equals(this.number), "Received transfer for a different account");
        final Money transferValueInAccountsCurrency = exchangeIfNecessary(transfer.getValue());

        final boolean transferExceedsReceiveLimit = transferValueInAccountsCurrency.isGreaterThan(transferReceiveLimit);
        if (transferExceedsReceiveLimit) {
            this.domainEvents = domainEvents.push(new MoneyTransferRejected(transfer.getReferenceNumber()));
            return;
        }

        this.balance = balance.plus(transferValueInAccountsCurrency);
        this.domainEvents = domainEvents.push(new MoneyTransferAccepted(transfer.getReferenceNumber()));
    }

    public void depositMoney(Money money) {
        money = exchangeIfNecessary(money);
        this.balance = this.balance.plus(money);
    }

    public List<DomainEvent> getEventsAndFlush() {
        final List<DomainEvent> eventsCopy = domainEvents;
        this.domainEvents = List.empty();
        return eventsCopy;
    }

    private Money exchangeIfNecessary(Money transferValue) {
        if (transferValue.getCurrencyUnit().equals(this.currency)) {
            return transferValue;
        }
        return currencyExchanger.exchange(transferValue, this.currency);
    }

    public Set<TransferReferenceNumber> getPendingTransfersReferenceNumbers() {
        return moneyBlockedOnTransfers.keySet();
    }
}
