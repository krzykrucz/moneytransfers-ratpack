package com.krzykrucz.transfers

import com.krzykrucz.transfers.domain.CurrencyExchanger
import com.krzykrucz.transfers.domain.account.AccountRepository
import ratpack.guice.BindingsImposition
import ratpack.impose.ImpositionsSpec
import ratpack.impose.UserRegistryImposition
import ratpack.registry.Registry
import ratpack.test.MainClassApplicationUnderTest

class TestApplicationWithMockedServices extends MainClassApplicationUnderTest {

    CurrencyExchanger exchanger = null

    Class<? extends AccountRepository> accountRepositoryClass = null

    Registry appRegistry

    TestApplicationWithMockedServices(Class<?> mainClass) {
        super(mainClass)
    }

    TestApplicationWithMockedServices(Class<?> mainClass, CurrencyExchanger exchanger) {
        super(mainClass)
        this.exchanger = exchanger
    }

    TestApplicationWithMockedServices(Class<?> mainClass, Class<? extends AccountRepository> repoClass) {
        super(mainClass)
        this.accountRepositoryClass = repoClass
    }

    @Override
    protected void addImpositions(ImpositionsSpec impositions) {
        impositions.add(UserRegistryImposition.of({
            this.appRegistry = it
            Registry.empty()
        }))

        if (exchanger != null) {
            impositions.add(BindingsImposition.of {
                it.bindInstance(CurrencyExchanger, this.exchanger)
            })
        }
        if (accountRepositoryClass != null) {
            impositions.add(BindingsImposition.of {
                it.bind(AccountRepository, accountRepositoryClass)
            })
        }
    }

    def <T> T getInstance(Class<T> classOf) {
        super.getAddress()
        appRegistry.get(classOf)
    }

}