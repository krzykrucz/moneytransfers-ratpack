package com.krzykrucz.transfers.domain.util

import com.krzykrucz.transfers.domain.CurrencyExchanger
import org.joda.money.CurrencyUnit
import org.joda.money.Money

class NoOpCurrencyExchanger implements CurrencyExchanger {
    @Override
    Money exchangeIfNecessary(Money money, CurrencyUnit currencyUnit) {
        money
    }
}
