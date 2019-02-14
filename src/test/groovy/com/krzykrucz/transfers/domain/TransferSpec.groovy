package com.krzykrucz.transfers.domain

import com.krzykrucz.transfers.domain.util.DomainTest

class TransferSpec extends DomainTest {

    def "should reject transfer beyond limits"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'USD'
        money USD_30_000 'deposited on account' '01'

        when:
        money(USD_15_001).transfered('01', '02')

        then:
        conditions.eventually {
            'balance of account'('01') == '$14999.00'
            'balance of account'('02') == '$0.00'
        }

        and:
        conditions.eventually {
            'transfer rejected event published'()
            'balance of account'('01') == '30000.00'
            'balance of account'('02') == '$0.00'
        }
    }

    def "should accept transfer within limits"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'USD'
        money USD_30_000 'deposited on account' '01'

        when:
        money(USD_15_000).transfered('01', '02')

        then:
        conditions.eventually {
            'transfer accepted event published'()
            'balance of account'('01') == '$15000.00'
            'balance of account'('02') == '$15000.00'
        }
    }

}
