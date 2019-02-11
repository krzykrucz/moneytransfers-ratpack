package com.krzykrucz.transfers

import com.krzykrucz.transfers.domain.CurrencyExchanger
import com.krzykrucz.transfers.domain.account.AccountRepository
import com.krzykrucz.transfers.infrastructure.persistence.AccountRepositorySpyProvider
import ratpack.guice.BindingsImposition
import ratpack.impose.ImpositionsSpec
import ratpack.impose.UserRegistryImposition
import ratpack.registry.Registry
import ratpack.test.MainClassApplicationUnderTest
import spock.mock.DetachedMockFactory

class TestApplicationWithMockedServices extends MainClassApplicationUnderTest {

    def exchanger = null
    def accountRepository = null

    Registry appRegistry

    TestApplicationWithMockedServices(Class<?> mainClass) {
        super(mainClass)
    }

    TestApplicationWithMockedServices(Class<?> mainClass, CurrencyExchanger exchanger) {
        super(mainClass)
        this.exchanger = exchanger
    }

    TestApplicationWithMockedServices(Class<?> mainClass, AccountRepository repository) {
        super(mainClass)
        this.accountRepository = repository
    }

    @Override
    protected void addImpositions(ImpositionsSpec impositions) {
        if (exchanger != null) {
            impositions.add(BindingsImposition.of {
                it.bindInstance(CurrencyExchanger, this.exchanger)
            })
        }
//        if (accountRepository != null) {
//            impositions.add(BindingsImposition.of {
//                it.bindInstance(AccountRepository, this.accountRepository)
//            })
//        }
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