package com.krzykrucz.transfers

import com.krzykrucz.transfers.domain.CurrencyExchanger
import com.krzykrucz.transfers.domain.account.AccountRepository
import com.krzykrucz.transfers.infrastructure.persistence.AccountRepositorySpyProvider
import ratpack.guice.BindingsImposition
import ratpack.impose.ImpositionsSpec
import ratpack.impose.UserRegistryImposition
import ratpack.registry.Registry
import ratpack.test.MainClassApplicationUnderTest

class TestApplicationWithMockedServices extends MainClassApplicationUnderTest {

    def exchanger = null

    Registry appRegistry

    TestApplicationWithMockedServices(Class<?> mainClass) {
        super(mainClass)
    }

    TestApplicationWithMockedServices(Class<?> mainClass, CurrencyExchanger exchanger) {
        super(mainClass)
        this.exchanger = exchanger
    }

    @Override
    protected void addImpositions(ImpositionsSpec impositions) {
        if (exchanger != null) {
            impositions.add(BindingsImposition.of {
                it.bindInstance(CurrencyExchanger.class, this.exchanger)
            })
        }
        impositions.add(UserRegistryImposition.of({
            this.appRegistry = it
            Registry.empty()
        }))

        impositions.add(BindingsImposition.of {
            it.providerType(AccountRepository, AccountRepositorySpyProvider)
        })
    }

    def <T> T getInstance(Class<T> classOf) {
        super.getAddress()
        appRegistry.get(classOf)
    }


}