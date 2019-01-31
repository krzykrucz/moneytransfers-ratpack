package com.krzykrucz.transfers.domain;

import com.krzykrucz.transfers.domain.account.MoneyTransfer;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public interface CurrencyExchanger {

    Money exchangeIfNecessary(Money money, CurrencyUnit currencyUnit);

    default MoneyTransfer exchangeIfNecessary(MoneyTransfer moneyTransfer, CurrencyUnit currencyUnit) {
        final Money money = moneyTransfer.getValue();
        final Money exchangedMoney = exchangeIfNecessary(money, currencyUnit);
        return new MoneyTransfer(
                moneyTransfer.getReferenceNumber(),
                exchangedMoney,
                moneyTransfer.getFrom(),
                moneyTransfer.getTo()
        );
    }

}
