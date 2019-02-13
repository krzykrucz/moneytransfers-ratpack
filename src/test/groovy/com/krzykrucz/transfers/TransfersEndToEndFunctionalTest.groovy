package com.krzykrucz.transfers

import com.krzykrucz.transfers.infrastructure.exchanger.ThrowingExternalCurrencyExchanger

// TODO mock AppServiceImpl, remaining tests move to domain tests
class TransfersEndToEndFunctionalTest extends IntegrationTest {

    def "should transfer money"() {
        when:
        'account created' '01', 'USD'
        'account created' '02', 'USD'

        then:
        'balance of account'('01') == '$0.00'
        'balance of account'('02') == '$0.00'

        when:
        money THIRTY_DOLLARS 'deposited on account' '01'
        money THIRTY_DOLLARS 'deposited on account' '02'

        then:
        'balance of account'('01') == '$30.00'
        'balance of account'('02') == '$30.00'

        when:
        money(TEN_DOLLARS).transfered('01', '02')

        then:
        'balance of account'('01') == '$20.00'
        'balance of account'('02') == '$40.00'
        and:
        'all responses are' 200
    }

    def "should reject transfer"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'USD'
        money USD_15_000 'deposited on account' '01'

        when:
        money(USD_15_000).transfered('01', '02')

        then:
        'balance of account'('01') == '$15000.00'
        'balance of account'('02') == '$0.00'
        and:
        'all responses are' 200
    }

    def "should transfer money with a different currency"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'EUR'

        when:
        money THIRTY_EURO 'deposited on account' '01'
        money THIRTY_DOLLARS 'deposited on account' '02'

        then:
        'balance of account'('01') == '$30.00'
        'balance of account'('02') == 'EUR30.00'

        when:
        money(TEN_EURO).transfered('02', '01')
        money(TEN_DOLLARS).transfered('01', '02')

        then:
        'balance of account'('01') == '$30.00'
        'balance of account'('02') == 'EUR30.00'

        and:
        'all responses are' 200
    }

    def "should return 400 for non-existent account"() {
        when:
        money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        'all responses are' 400
    }

    def "should return 400 for money shortage"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'USD'

        when:
        def response = money(TEN_DOLLARS).transfered('01', '02')

        then:
        response.statusCode == 400
    }

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
