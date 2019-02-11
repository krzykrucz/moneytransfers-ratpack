package com.krzykrucz.transfers.infrastructure.exchanger

import com.krzykrucz.transfers.application.error.OptimisticLockException
import com.krzykrucz.transfers.domain.CurrencyExchanger
import org.joda.money.CurrencyUnit
import org.joda.money.Money

class ThrowingExternalCurrencyExchanger implements CurrencyExchanger {
    final Exception throwable

    private ThrowingExternalCurrencyExchanger(Exception exceptionToThrow) {
        throwable = exceptionToThrow
    }

    static ThrowingExternalCurrencyExchanger withExceptionText(String exceptionText) {
        new ThrowingExternalCurrencyExchanger(new RuntimeException(exceptionText))
    }

    static ThrowingExternalCurrencyExchanger withOptimisticLockException() {
        new ThrowingExternalCurrencyExchanger(new OptimisticLockException())
    }

    @Override
    Money exchangeIfNecessary(Money money, CurrencyUnit targetCurrencyUnit) {
        throw throwable
    }
}