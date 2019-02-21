package com.krzykrucz.transfers.adapters.exchanger;

import com.krzykrucz.transfers.domain.CurrencyExchanger;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class IdentityCurrencyExchanger implements CurrencyExchanger {
    @Override
    public Money exchangeIfNecessary(Money money, CurrencyUnit currencyUnit) {
        return Money.of(currencyUnit, money.getAmount());
    }
}
