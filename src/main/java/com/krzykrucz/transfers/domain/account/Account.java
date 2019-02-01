package com.krzykrucz.transfers.domain.account;

import com.krzykrucz.transfers.domain.account.event.MoneyTransferAccepted;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferCommissioned;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferRejected;
import com.krzykrucz.transfers.domain.common.Aggregate;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import static com.krzykrucz.transfers.domain.common.DomainException.checkDomainState;

@EqualsAndHashCode(of = "id", callSuper = true)
// TODO make immutable
// TODO ensure idempotency (test)
public class Account extends Aggregate { // TODO extend aggregate root

    @Getter
    private final AccountIdentifier id;

    @Getter
    private final AccountNumber number;

    @Getter
    private final CurrencyUnit currency;
    private final TransferReceiveLimitPolicy transferReceiveLimit;
    private final AccountDomainValidator validator;
    @Getter
    private Money balance;
    // TODO enclose with a value object
    private Map<TransferReferenceNumber, Money> moneyBlockedOnTransfers;

    public Account(AccountIdentifier id, AccountNumber number, CurrencyUnit currency) {
        super(0);
        this.id = id;
        this.balance = Money.zero(currency);
        this.currency = currency;
        this.number = number;
        this.moneyBlockedOnTransfers = HashMap.empty();
        this.transferReceiveLimit = new TransferReceiveLimitPolicy(currency);
        this.validator = new AccountDomainValidator();
    }

    private Account(long version, AccountIdentifier id, AccountNumber number, CurrencyUnit currency,
                    TransferReceiveLimitPolicy transferReceiveLimit, AccountDomainValidator validator, Money balance,
                    Map<TransferReferenceNumber, Money> moneyBlockedOnTransfers) {
        super(version);
        this.id = id;
        this.number = number;
        this.currency = currency;
        this.transferReceiveLimit = transferReceiveLimit;
        this.validator = validator;
        this.balance = balance;
        this.moneyBlockedOnTransfers = moneyBlockedOnTransfers;
    }

    public void commissionTransferTo(AccountNumber anotherAccount, Money transferValue) {
        validator.validateOutcomingTransfer(transferValue);

        this.balance = balance.minus(transferValue);

        final MoneyTransfer transfer = MoneyTransfer.generateMoneyTransfer(transferValue, this.number, anotherAccount);
        this.moneyBlockedOnTransfers = moneyBlockedOnTransfers.put(transfer.getReferenceNumber(), transferValue);

        publishEvent(new MoneyTransferCommissioned(this.id, transferValue, transfer));
    }

    public void confirmTransfer(TransferReferenceNumber transferReferenceNumber) {
        validator.checkExists(transferReferenceNumber);

        this.moneyBlockedOnTransfers = moneyBlockedOnTransfers.remove(transferReferenceNumber);
    }

    public void rejectTransfer(TransferReferenceNumber transferReferenceNumber) {
        validator.checkExists(transferReferenceNumber);

        this.moneyBlockedOnTransfers.get(transferReferenceNumber)
                .peek(moneyToAddBack -> this.balance = balance.plus(moneyToAddBack));
        this.moneyBlockedOnTransfers = moneyBlockedOnTransfers.remove(transferReferenceNumber);
    }

    public void receiveTransfer(MoneyTransfer transfer) {
        validator.validateIncomingTransfer(transfer);

        if (transferReceiveLimit.isExceededFor(transfer)) {
            publishEvent(new MoneyTransferRejected(this.id, transfer.getReferenceNumber()));
            return;
        }

        this.balance = balance.plus(transfer.getValue());
        publishEvent(new MoneyTransferAccepted(this.id, transfer.getReferenceNumber()));
    }


    public void depositMoney(Money money) {
        validator.checkCurrency(money);

        this.balance = this.balance.plus(money);
    }

    public Set<TransferReferenceNumber> getPendingTransfers() {
        return moneyBlockedOnTransfers.keySet();
    }

    public Account copy() {
        return new Account(getVersion(), id, number, currency, transferReceiveLimit, validator, balance, moneyBlockedOnTransfers);
    }

    private class AccountDomainValidator {
        private void validateIncomingTransfer(MoneyTransfer transfer) {
            checkCurrency(transfer.getValue());
            checkDomainState(transfer.getTo().equals(number), "Received transfer for a different account");
        }

        private void validateOutcomingTransfer(Money transferValue) {
            checkCurrency(transferValue);
            checkDomainState(!balance.isLessThan(transferValue), "Not enough money on account to do a transfer");
        }

        private void checkCurrency(Money money) {
            checkDomainState(money.getCurrencyUnit().equals(currency), "Received a transfer with a different currency");
        }

        private void checkExists(TransferReferenceNumber transferReferenceNumber) {
            checkDomainState(moneyBlockedOnTransfers.keySet()
                            .contains(transferReferenceNumber),
                    "No pending transfer with that reference"
            );
        }
    }

    private class TransferReceiveLimitPolicy {
        private static final int DEFAULT_TRANSFER_RECEIVE_LIMIT = 10000;

        private final Money limit;

        private TransferReceiveLimitPolicy(CurrencyUnit currencyUnit) {
            this.limit = Money.of(currencyUnit, DEFAULT_TRANSFER_RECEIVE_LIMIT);
        }

        boolean isExceededFor(MoneyTransfer moneyTransfer) {
            return moneyTransfer.getValue()
                    .isGreaterThan(limit);
        }
    }
}
