package com.krzykrucz.transfers

import com.krzykrucz.transfers.application.error.OptimisticLockException
import com.krzykrucz.transfers.domain.account.AccountRepository

class TransfersConcurrencyTest extends IntegrationTest {

    AccountRepository repository

    def setup() {
        app = new TestApplicationWithMockedServices(MoneyTransfersApplication)
        repository = app.getInstance(AccountRepository)
    }

    def "should perform command on 1 retry"() {
        given:
        'account created'('01', 'USD')
        repository.save(_) >> { throw new OptimisticLockException() } >> {}

        when:
        money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        'all responses are' 200

    }

    def "should fail command on 4 optimistic locks"() {
        given:
        4 * repository.save(_) >>
                { throw new OptimisticLockException() } >>
                { throw new OptimisticLockException() } >>
                { throw new OptimisticLockException() } >>
                { throw new OptimisticLockException() } >>
                {}

        when:
        money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        'all responses are' 409
    }
}
