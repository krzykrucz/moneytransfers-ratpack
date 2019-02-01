package com.krzykrucz.transfers.domain.account;

import com.krzykrucz.transfers.domain.account.event.MoneyTransferAccepted;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferCommissioned;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferRejected;
import com.krzykrucz.transfers.domain.common.AggregateRoot;
import io.vavr.collection.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import static com.krzykrucz.transfers.domain.common.DomainException.checkDomainState;

@EqualsAndHashCode(of = "id", callSuper = true)
// TODO make immutable ??
// TODO ensure idempotency (test)
public class Account extends AggregateRoot {

    @Getter
    private final AccountIdentifier id;
    @Getter
    private final AccountNumber accountNumber;
    @Getter
    private final CurrencyUnit currency;
    private final TransferReceiveLimitPolicy transferReceiveLimit;
    @Getter
    private Money balance;
    private PendingTransfers pendingTransfers;

    public Account(AccountIdentifier id, AccountNumber accountNumber, CurrencyUnit currency) {
        super(0);
        this.id = id;
        this.balance = Money.zero(currency);
        this.currency = currency;
        this.accountNumber = accountNumber;
        this.pendingTransfers = PendingTransfers.empty();
        this.transferReceiveLimit = new TransferReceiveLimitPolicy(currency);
    }

    private Account(long version, AccountIdentifier id, AccountNumber accountNumber, CurrencyUnit currency,
                    TransferReceiveLimitPolicy transferReceiveLimit, Money balance,
                    PendingTransfers pendingTransfers) {
        super(version);
        this.id = id;
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.transferReceiveLimit = transferReceiveLimit;
        this.balance = balance;
        this.pendingTransfers = pendingTransfers;
    }

    public void commissionTransferTo(AccountNumber anotherAccount, Money transferValue) {
        validateOutcomingTransfer(transferValue);

        final MoneyTransfer transfer = MoneyTransfer.generateNewTransfer()
                .withValue(transferValue)
                .from(accountNumber)
                .to(anotherAccount)
                .build();
        beginATransfer(transfer);

        decreaseBalanceBy(transferValue);

        publishEvent(new MoneyTransferCommissioned(this.id, transferValue, transfer));
    }

    public void confirmTransfer(TransferReferenceNumber transferReferenceNumber) {
        removePendingTransfer(transferReferenceNumber);
    }

    public void rejectTransfer(TransferReferenceNumber transferReferenceNumber) {
        final Money moneyToAddBack = pendingTransfers.getMoneyBlockedOn(transferReferenceNumber);
        removePendingTransfer(transferReferenceNumber);
        increaseBalanceBy(moneyToAddBack);
    }

    public void receiveTransfer(MoneyTransfer transfer) {
        validateIncomingTransfer(transfer);

        if (transferReceiveLimit.isExceededFor(transfer)) {
            publishEvent(new MoneyTransferRejected(this.id, transfer.getReferenceNumber()));
            return;
        }

        increaseBalanceBy(transfer.getValue());
        publishEvent(new MoneyTransferAccepted(this.id, transfer.getReferenceNumber()));
    }

    public void depositMoney(Money money) {
        checkCurrency(money);

        increaseBalanceBy(money);
    }

    public Set<TransferReferenceNumber> getPendingTransfers() {
        return pendingTransfers.getReferenceNumbers();
    }

    public Account copy() {
        return new Account(getVersion(), id, accountNumber, currency, transferReceiveLimit, balance, pendingTransfers);
    }

    private void removePendingTransfer(TransferReferenceNumber transferReferenceNumber) {
        pendingTransfers = pendingTransfers.removeTransfer(transferReferenceNumber);
    }

    private void beginATransfer(MoneyTransfer transfer) {
        pendingTransfers = pendingTransfers.addTransfer(transfer);
    }

    private void decreaseBalanceBy(Money transferValue) {
        balance = balance.minus(transferValue);
    }

    private void increaseBalanceBy(Money money) {
        balance = balance.plus(money);
    }

    private void validateIncomingTransfer(MoneyTransfer transfer) {
        checkCurrency(transfer.getValue());
        checkDomainState(transfer.getTo().equals(accountNumber), "Received transfer for a different account");
    }

    private void validateOutcomingTransfer(Money transferValue) {
        checkCurrency(transferValue);
        checkDomainState(!balance.isLessThan(transferValue), "Not enough money on account to do a transfer");
    }

    private void checkCurrency(Money money) {
        checkDomainState(money.getCurrencyUnit().equals(currency), "Received a transfer with a different currency");
    }

}
