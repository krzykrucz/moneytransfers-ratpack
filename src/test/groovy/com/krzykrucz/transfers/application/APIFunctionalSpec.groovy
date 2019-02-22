package com.krzykrucz.transfers.application

import com.krzykrucz.transfers.adapters.persistence.OptimisticLockException
import com.krzykrucz.transfers.application.util.ApplicationTest
import com.krzykrucz.transfers.domain.account.Account
import com.krzykrucz.transfers.domain.account.AccountIdentifier
import com.krzykrucz.transfers.domain.account.AccountNumber
import com.krzykrucz.transfers.domain.common.DomainException
import org.joda.money.Money
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
        1             | new OptimisticLockException() || 200
        4             | new OptimisticLockException() || 500
        4             | new DomainException('')       || 400
//        1             | new DomainException('')         || 400 // TODO implement
    }

    def "should respond 200 when depositing money"() {
        when:
        money() 'deposited on account'()

        then:
        'all responses are' 200
    }

    def "should respond 200 when opening account"() {
        when:
        'account created'('01', 'USD')

        then:
        'all responses are' 200
    }

    def "should respond 200 when transfering money"() {
        when:
        money().transfered('01', '02')

        then:
        'all responses are' 200
    }

    def "should query correct account information"() {
        given:
        'application will return' account('01', TEN_DOLLARS)

        expect:
        'balance of account'('01') == '$10.00'
    }

    def account(String number, Money balance) {
        def account = new Account(AccountIdentifier.generate(), new AccountNumber(number), balance.currencyUnit)
        account.depositMoney(balance)
        account
    }

}
