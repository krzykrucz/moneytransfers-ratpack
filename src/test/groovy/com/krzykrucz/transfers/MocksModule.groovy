package com.krzykrucz.transfers

import com.google.inject.AbstractModule
import com.krzykrucz.transfers.domain.account.AccountRepository

class MocksModule extends AbstractModule {

    @Override
    protected void configure() {
        def app = new TestApplicationWithMockedServices(MoneyTransfersApplication)
        def spyAccountRepository = app.getInstance(AccountRepository)

        bind(AccountRepository).toInstance(spyAccountRepository)
        bind(TestApplicationWithMockedServices).toInstance(app)
    }
}
