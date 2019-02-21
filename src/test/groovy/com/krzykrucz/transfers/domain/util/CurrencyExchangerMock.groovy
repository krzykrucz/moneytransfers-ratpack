package com.krzykrucz.transfers.domain.util

import com.krzykrucz.transfers.domain.CurrencyExchanger
import org.joda.money.CurrencyUnit
import org.joda.money.Money

class CurrencyExchangerMock implements CurrencyExchanger {

    def oneToOneRate

    @Override
    Money exchangeIfNecessary(Money money, CurrencyUnit currencyUnit) {
        if (oneToOneRate) {
            return Money.of(currencyUnit, money.getAmount())
        }
        return money
    }

    def convertWithOneToOneRate() {
        oneToOneRate = true
    }

    def reset() {
        oneToOneRate = false
    }
}
