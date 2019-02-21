package com.krzykrucz.transfers.domain

import com.krzykrucz.transfers.domain.common.DomainException
import com.krzykrucz.transfers.domain.util.DomainTest

class NewAccountSpec extends DomainTest {

    def "should create empty account"() {
        when:
        'account created' '01', 'USD'

        then:
        'balance of account'('01') == '$0.00'
    }

    def "should deposit money"() {
        given:
        'account created' '01', 'USD'

        when:
        money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        'balance of account'('01') == '$30.00'
    }

    def "should not deposit money to non-existent account"() {
        when:
        money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        thrown DomainException
    }
}