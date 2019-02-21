package com.krzykrucz.transfers.adapters.exchanger

import com.krzykrucz.transfers.adapters.persistence.OptimisticLockException
import com.krzykrucz.transfers.domain.CurrencyExchanger
import org.joda.money.CurrencyUnit
import org.joda.money.Money

@Deprecated
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