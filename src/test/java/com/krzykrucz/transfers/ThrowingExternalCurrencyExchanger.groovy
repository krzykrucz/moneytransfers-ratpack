package com.krzykrucz.transfers

import com.krzykrucz.transfers.infrastructure.ExternalCurrencyExchanger
import org.joda.money.CurrencyUnit
import org.joda.money.Money

class ThrowingExternalCurrencyExchanger extends ExternalCurrencyExchanger {
    String text

    private ThrowingExternalCurrencyExchanger(String text) {
        this.text = text
    }

    static ThrowingExternalCurrencyExchanger withExceptionText(String exceptionText) {
        new ThrowingExternalCurrencyExchanger(exceptionText)
    }

    @Override
    Money exchange(Money money, CurrencyUnit targetCurrencyUnit) {
        throw new RuntimeException(text)
    }
}