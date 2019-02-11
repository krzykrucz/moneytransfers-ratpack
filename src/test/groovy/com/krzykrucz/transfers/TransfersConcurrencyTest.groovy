package com.krzykrucz.transfers

import com.google.inject.Inject
import com.krzykrucz.transfers.application.error.OptimisticLockException
import com.krzykrucz.transfers.domain.account.AccountRepository
import spock.guice.UseModules

@UseModules(MocksModule)
class TransfersConcurrencyTest extends IntegrationTest {

    @Inject
    AccountRepository repository

    @Inject
    TestApplicationWithMockedServices application

    def setup() {
        app = application
    }

    def "should perform command on 1 retry"() {
        given:
        'account created'('01', 'USD')
        2 * repository.save(_) >> { throw new OptimisticLockException() } >> {}

        when:
        money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        'all responses are' 200

    }

    def "should fail command on 4 optimistic locks"() {
        given:
        'account created'('01', 'USD')
        4 * repository.save(_) >>
                { throw new OptimisticLockException() } >>
                { throw new OptimisticLockException() } >>
                { throw new OptimisticLockException() } >>
                { throw new OptimisticLockException() } >>
                {}

        when:
        def response = money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        response.statusCode == 409
    }
}
