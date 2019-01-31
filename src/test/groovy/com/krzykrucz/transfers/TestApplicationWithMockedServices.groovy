package com.krzykrucz.transfers

import com.krzykrucz.transfers.domain.CurrencyExchanger
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import ratpack.guice.BindingsImposition
import ratpack.impose.ImpositionsSpec
import ratpack.test.MainClassApplicationUnderTest

class TestApplicationWithMockedServices extends MainClassApplicationUnderTest {

    def exchanger = new NoOpCurrencyExchanger()

    TestApplicationWithMockedServices(Class<?> mainClass) {
        super(mainClass)
    }

    TestApplicationWithMockedServices(Class<?> mainClass, CurrencyExchanger exchanger) {
        super(mainClass)
        this.exchanger = exchanger
    }

    @Override
    protected void addImpositions(ImpositionsSpec impositions) {
        impositions.add(BindingsImposition.of {
            it.bindInstance(CurrencyExchanger.class, this.exchanger)
        })
    }

    class NoOpCurrencyExchanger implements CurrencyExchanger {
        @Override
        Money exchange(Money money, CurrencyUnit targetCurrencyUnit) {
            Money.of(targetCurrencyUnit, money.amount)
        }
    }
}