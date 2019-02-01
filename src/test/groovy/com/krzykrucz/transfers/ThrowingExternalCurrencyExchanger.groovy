package com.krzykrucz.transfers

import com.krzykrucz.transfers.domain.CurrencyExchanger
import com.krzykrucz.transfers.infrastructure.persistence.OptimisticLockException
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