package com.krzykrucz.transfers.application

import com.krzykrucz.transfers.application.util.IntegrationTest
import spock.util.concurrent.PollingConditions

class TransferEndToEndSpec extends IntegrationTest {

    def conditions = new PollingConditions(timeout: 10, factor: 1.25)

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
        conditions.eventually {
            'balance of account'('01') == '$20.00'
            'balance of account'('02') == '$40.00'
        }
        and:
        'all responses are' 200
    }

}
