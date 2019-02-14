package com.krzykrucz.transfers.application

import com.krzykrucz.transfers.application.util.ApplicationTest
import org.junit.Ignore
import spock.lang.Unroll

class ResilienceSpec extends ApplicationTest {

    @Unroll
    def "depositing money should be resilient to up to 3 retries"() {
        given:
        "number of times command will fail"(subsiquentDomainFails, 'depositMoney')

        when:
        def response = money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        true
        response.statusCode == expectedResponseCode

        where:
        subsiquentDomainFails || expectedResponseCode
        0                     || 200
        3                     || 200
        4                     || 500
    }

    @Unroll
    def "opening account should be resilient to up to 3 retries"() {
        given:
        "number of times command will fail"(subsiquentDomainFails, 'openAccount')

        when:
        def response = 'account created' '01', 'USD'

        then:
        true
        response.statusCode == expectedResponseCode

        where:
        subsiquentDomainFails || expectedResponseCode
        0                     || 200
        3                     || 200
        4                     || 500
    }

    @Unroll
    def "commissioning transfer should be resilient to up to 3 retries"() {
        given:
        "number of times command will fail"(subsiquentDomainFails, 'transfer')

        when:
        def response = money THIRTY_DOLLARS transfered '01', '02'

        then:
        true
        response.statusCode == expectedResponseCode

        where:
        subsiquentDomainFails || expectedResponseCode
        0                     || 200
        3                     || 200
        4                     || 500
    }

    @Unroll
    def "accepting transfer should be resilient to up to 3 retries"() {
        // TODO
    }

    @Unroll
    def "rejecting transfer should be resilient to up to 3 retries"() {
        // TODO
    }

    @Unroll
    def "receiving transfer should be resilient to up to 3 retries"() {
        //tODO
    }

    @Ignore
    // TODO implement
    def "should recover transfer sender when receiving transfer fails"() {
        given:
        "number of times command will fail"(4, 'receiveTransfer')

        when:
        money THIRTY_DOLLARS transfered '01', '02'

        then:
        1 * domainAPI.rejectTransfer(_)
    }

}
