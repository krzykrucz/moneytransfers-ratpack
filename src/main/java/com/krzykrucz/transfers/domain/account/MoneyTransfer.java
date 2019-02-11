package com.krzykrucz.transfers.domain.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.joda.money.Money;

import static com.krzykrucz.transfers.domain.common.DomainException.checkDomainState;
import static com.krzykrucz.transfers.domain.common.DomainException.checkNotNull;


@Getter
@EqualsAndHashCode(of = "referenceNumber")
public class MoneyTransfer { // TODO make all values validation - write tests

    private final TransferReferenceNumber referenceNumber;
    private final Money value;
    private final AccountNumber from;
    private final AccountNumber to;

    public MoneyTransfer(TransferReferenceNumber referenceNumber, Money value, AccountNumber from, AccountNumber to) {
        this.referenceNumber = checkNotNull(referenceNumber, "Transfer Reference Number");
        this.value = checkNotNull(value, "Transfer value");
        checkDomainState(value.getAmountMajorInt() >= 0, "Transfer less than zero");
        this.from = checkNotNull(from, "Transfer source");
        this.to = checkNotNull(to, "Transfer destination");
    }

    public static Builder generateNewTransfer() {
        return new Builder();
    }

    public static final class Builder {
        private Money value;
        private AccountNumber from;
        private AccountNumber to;

        private Builder() {
        }

        public Builder withValue(Money value) {
            this.value = value;
            return this;
        }

        public Builder from(AccountNumber from) {
            this.from = from;
            return this;
        }

        public Builder to(AccountNumber to) {
            this.to = to;
            return this;
        }

        public MoneyTransfer build() {
            return new MoneyTransfer(TransferReferenceNumber.generate(), value, from, to);
        }
    }
}
