package com.krzykrucz.transfers

import com.krzykrucz.transfers.infrastructure.ExternalCurrencyExchanger
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import ratpack.guice.BindingsImposition
import ratpack.impose.ImpositionsSpec
import ratpack.test.MainClassApplicationUnderTest;

class TestApplicationWithMockedServices extends MainClassApplicationUnderTest {

    def exchanger = new NoOpCurrencyExchanger()

    TestApplicationWithMockedServices(Class<?> mainClass) {
        super(mainClass)
    }

    TestApplicationWithMockedServices(Class<?> mainClass, ExternalCurrencyExchanger exchanger) {
        super(mainClass)
        this.exchanger = exchanger
    }

    @Override
    protected void addImpositions(ImpositionsSpec impositions) {
        impositions.add(BindingsImposition.of {
            it.bindInstance(ExternalCurrencyExchanger.class, this.exchanger)
        })
    }

    class NoOpCurrencyExchanger extends ExternalCurrencyExchanger {
        @Override
        Money exchange(Money money, CurrencyUnit targetCurrencyUnit) {
            Money.of(targetCurrencyUnit, money.amount)
        }
    }
}