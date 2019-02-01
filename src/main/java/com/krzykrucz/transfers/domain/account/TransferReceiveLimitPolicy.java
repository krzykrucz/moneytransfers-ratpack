package com.krzykrucz.transfers.domain.account;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

class TransferReceiveLimitPolicy {
    private static final int DEFAULT_TRANSFER_RECEIVE_LIMIT = 10000;

    private final Money limit;

    TransferReceiveLimitPolicy(CurrencyUnit currencyUnit) {
        this.limit = Money.of(currencyUnit, DEFAULT_TRANSFER_RECEIVE_LIMIT);
    }

    boolean isExceededFor(MoneyTransfer moneyTransfer) {
        return moneyTransfer.getValue()
                .isGreaterThan(limit);
    }
}
