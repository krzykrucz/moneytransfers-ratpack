package com.krzykrucz.transfers.application

import com.krzykrucz.transfers.application.error.OptimisticLockException
import com.krzykrucz.transfers.application.util.ApplicationTest
import com.krzykrucz.transfers.domain.common.DomainException
import com.krzykrucz.transfers.infrastructure.exchanger.ThrowingExternalCurrencyExchanger
import spock.lang.Ignore
import spock.lang.Unroll

class APIFunctionalSpec extends ApplicationTest {

    @Unroll
    def "should respond with #response to #numberOfFails #exception"() {
        given:
        'number of times command will fail'(numberOfFails, 'depositMoney', exception)

        when:
        money() 'deposited on account'()

        then:
        'all responses are' response

        where:
        numberOfFails | exception                     || response
        0             | new RuntimeException()        || 200
        4             | new RuntimeException()        || 500
        4             | new OptimisticLockException() || 409
        1             | new OptimisticLockException() || 200
        4             | new DomainException('')       || 400
//        1             | new DomainException('')         || 400 // TODO implement
    }

    // TODO test ony API calls to all endpoints

    def "should query correct account information"() {
        // TODO
    }

    // TODO move all below to domain tests


    @Ignore
    def "should return 400 for non-existent account"() {
        when:
        money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        'all responses are' 400
    }

    @Ignore
    def "should return 400 for money shortage"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'USD'

        when:
        def response = money(TEN_DOLLARS).transfered('01', '02')

        then:
        response.statusCode == 400
    }

    @Ignore
    def "should return 409 for optimistic lock"() {
        given:
        'mocked currency exchanger' ThrowingExternalCurrencyExchanger.withOptimisticLockException()
        'account created' '01', 'USD'

        when:
        def response = money THIRTY_EURO 'deposited on account' '01'

        then:
        response.statusCode == 409
        response.body.text == 'Conflict modifying multiple accounts at the same time'
    }

    @Ignore
    def "should return 500 for unknown error"() {
        given:
        'mocked currency exchanger' ThrowingExternalCurrencyExchanger.withExceptionText('error')
        'account created' '01', 'USD'

        when:
        def response = money THIRTY_EURO 'deposited on account' '01'

        then:
        response.statusCode == 500
        response.body.text == 'error'
    }


}
