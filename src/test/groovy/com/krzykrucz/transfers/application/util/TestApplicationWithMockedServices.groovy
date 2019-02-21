package com.krzykrucz.transfers.application.util

import com.krzykrucz.transfers.application.AccountApplicationService
import com.krzykrucz.transfers.domain.account.AccountRepository
import ratpack.guice.BindingsImposition
import ratpack.impose.ImpositionsSpec
import ratpack.test.MainClassApplicationUnderTest

class TestApplicationWithMockedServices extends MainClassApplicationUnderTest {

    AccountApplicationService service = null
    AccountRepository repository = null

    TestApplicationWithMockedServices(Class<?> mainClass) {
        super(mainClass)
    }

    TestApplicationWithMockedServices(Class<?> mainClass, AccountApplicationService service, AccountRepository repository) {
        super(mainClass)
        this.service = service
        this.repository = repository
    }

    @Override
    protected void addImpositions(ImpositionsSpec impositions) {
        if (repository != null && service != null) {
            impositions.add(BindingsImposition.of {
                it.bindInstance(AccountRepository, this.repository)
                it.bindInstance(AccountApplicationService, service)
            })
        }
    }

}