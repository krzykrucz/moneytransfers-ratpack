package com.krzykrucz.transfers

import com.krzykrucz.transfers.application.error.OptimisticLockException
import com.krzykrucz.transfers.domain.account.Account
import com.krzykrucz.transfers.domain.account.AccountRepository
import com.krzykrucz.transfers.infrastructure.persistence.InMemoryThrowingAccountRepository
import spock.lang.Ignore

import java.util.function.Predicate

// TODO mock AppServiceImpl
class TransfersResilienceTest extends IntegrationTest {

    InMemoryThrowingAccountRepository repository

    def setup() {
        app = new TestApplicationWithMockedServices(MoneyTransfersApplication, InMemoryThrowingAccountRepository)
        def repository = app.getInstance(AccountRepository)
        assert repository instanceof InMemoryThrowingAccountRepository
        this.repository = (InMemoryThrowingAccountRepository) repository
    }

    def "should perform command on 1 retry"() {
        given:
        'account created'('01', 'USD')
        whenSavingAccountThrow(new OptimisticLockException(), 1)

        when:
        money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        timesAccountShouldBeSaved(3)
        'all responses are' 200

    }

    def "should fail command on 4 exceptions"() {
        given:
        'account created'('01', 'USD')
        whenSavingAccountThrow(new OptimisticLockException(), 4)

        when:
        def response = money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        timesAccountShouldBeSaved(5)
        response.statusCode == 409
    }

    @Ignore
    def "should recover sender when receiving transfer fails"() {
        given:
        'account created'('01', 'USD') // 1
        'account created'('02', 'USD') // 2
        money THIRTY_DOLLARS 'deposited on account' '01' //3

        expect:
        'balance of account'('01') == '$30.00'
        'balance of account'('02') == '$0.00'

        when:
        def account02 = { it.accountNumber.toString() == '02' }
        whenSavingAccountThrow(new OptimisticLockException(), 4, account02)

        and:
        money THIRTY_DOLLARS transfered '01', '02'
        wait(2000)

        then:
        'all responses are' 200
        'balance of account'('02') == '$0.00'
        'balance of account'('01') == '$30.00'
        timesAccountShouldBeSaved(9)
    }

    def whenSavingAccountThrow(Exception exception = new RuntimeException(),
                               int times = 1,
                               Predicate<Account> when = { true }) {
        repository.throwExceptionsOnSave(exception, times, when)
    }

    def timesAccountShouldBeSaved(int times) {
        repository.timesSaveShouldBeInvoked(times)
    }
}
