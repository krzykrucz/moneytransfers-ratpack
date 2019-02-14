package com.krzykrucz.transfers.domain

import com.krzykrucz.transfers.domain.common.DomainException
import com.krzykrucz.transfers.domain.util.DomainTest

class ExchangingMoneySpec extends DomainTest {

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

        then:
        conditions.eventually {
            'balance of account'('01') == '$60.00'
            'balance of account'('02') == 'EUR0.00'
        }

        when:
        money(TEN_DOLLARS).transfered('01', '02')

        then:
        conditions.eventually {
            'balance of account'('01') == '$30.00'
            'balance of account'('02') == 'EUR30.00'
        }
    }


    def "should reject commissioning transfer with a different currency"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'USD'
        money THIRTY_DOLLARS 'deposited on account' '01'

        when:
        money(TEN_EURO).transfered('01', '02')

        then:
        thrown(DomainException)
    }

}
