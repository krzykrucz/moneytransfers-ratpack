package com.krzykrucz.transfers.domain

import com.krzykrucz.transfers.domain.common.DomainException
import com.krzykrucz.transfers.domain.util.DomainTest
import org.joda.money.Money
import spock.lang.Unroll

import static org.joda.money.CurrencyUnit.USD

class TransferSpec extends DomainTest {

    def "should reject transfer beyond limits"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'USD'
        money USD_30_000 'deposited on account' '01'

        when:
        money(USD_15_001).transfered('01', '02')

        then:
        'balance of account'('01') == '$30000.00'
        'balance of account'('02') == '$0.00'
    }

    def "should accept transfer within limits"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'USD'
        money USD_30_000 'deposited on account' '01'

        when:
        money(USD_15_000).transfered('01', '02')

        then:
        'balance of account'('01') == '$15000.00'
        'balance of account'('02') == '$15000.00'
    }

    def "should not transfer money for money shortage"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'USD'

        when:
        money(TEN_DOLLARS).transfered('01', '02')

        then:
        thrown DomainException
    }

    def "should reject transfer less than zero"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'USD'

        when:
        money(Money.of(USD, -1)).transfered('01', '02')

        then:
        thrown DomainException
    }

    @Unroll
    def "should reject transfer for a wrong account"() {
        given:
        'account created' '01', 'USD'
        'account created' '02', 'USD'

        when:
        money(TEN_DOLLARS).transfered(account1, account2)

        then:
        thrown DomainException

        where:
        account1 | account2
        '01'     | null
        null     | '02'
        '03'     | '02'
        '1'      | '02'
    }

}
