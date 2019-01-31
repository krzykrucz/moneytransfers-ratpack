package com.krzykrucz.transfers

import com.krzykrucz.transfers.domain.CurrencyExchanger
import org.joda.money.CurrencyUnit
import org.joda.money.Money

class ThrowingExternalCurrencyExchanger implements CurrencyExchanger {
    String text

    private ThrowingExternalCurrencyExchanger(String text) {
        this.text = text
    }

    static ThrowingExternalCurrencyExchanger withExceptionText(String exceptionText) {
        new ThrowingExternalCurrencyExchanger(exceptionText)
    }

    @Override
    Money exchangeIfNecessary(Money money, CurrencyUnit targetCurrencyUnit) {
        throw new RuntimeException(text)
    }
}