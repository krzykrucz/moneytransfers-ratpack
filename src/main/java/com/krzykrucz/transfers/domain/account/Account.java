package com.krzykrucz.transfers.domain.account;

import com.krzykrucz.transfers.domain.Aggregate;
import com.krzykrucz.transfers.domain.CurrencyExchanger;
import com.krzykrucz.transfers.domain.DomainEvent;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferAccepted;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferCommissioned;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferRejected;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import static com.google.common.base.Preconditions.checkState;

@EqualsAndHashCode(of = "id", callSuper = true)
// TODO make immutable
// TODO ensure idempotency (test)
public class Account extends Aggregate { // TODO extend aggregate root

    // TODO handle versions


    // TODO enclose with a policy
    private static final int DEFAULT_TRANSFER_RECEIVE_LIMIT = 10000;

    @Getter
    private final AccountIdentifier id;

    @Getter
    private final AccountNumber number;

    // TODO move outside of the aggregate
    private final CurrencyExchanger currencyExchanger;
    @Getter
    private Money balance;
    private final CurrencyUnit currency;
    // TODO enclose with a value object
    private Map<TransferReferenceNumber, Money> moneyBlockedOnTransfers;
    // TODO replace with domain events lib
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
        final Money transferValueInAccountsCurrency = exchangeIfNecessary(transferValue); // TODO replace with currency check
        checkState(!balance.isLessThan(transferValueInAccountsCurrency), "Not enough money on account to do a transfer");

        this.balance = balance.minus(transferValueInAccountsCurrency);

        final MoneyTransfer transfer = MoneyTransfer.generateMoneyTransfer(transferValue, this.number, anotherAccount);
        this.moneyBlockedOnTransfers = moneyBlockedOnTransfers.put(transfer.getReferenceNumber(), transferValueInAccountsCurrency);

        this.domainEvents = domainEvents.push(new MoneyTransferCommissioned(this.id, transferValueInAccountsCurrency, transfer));
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
        // TODO currency check
        checkState(transfer.getTo().equals(this.number), "Received transfer for a different account");
        final Money transferValueInAccountsCurrency = exchangeIfNecessary(transfer.getValue());

        final boolean transferExceedsReceiveLimit = transferValueInAccountsCurrency.isGreaterThan(transferReceiveLimit);
        if (transferExceedsReceiveLimit) {
            this.domainEvents = domainEvents.push(new MoneyTransferRejected(this.id, transfer.getReferenceNumber()));
            return;
        }

        this.balance = balance.plus(transferValueInAccountsCurrency);
        this.domainEvents = domainEvents.push(new MoneyTransferAccepted(this.id, transfer.getReferenceNumber()));
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
