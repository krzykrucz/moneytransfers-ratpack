package com.krzykrucz.transfers.adapters.persistence

import com.krzykrucz.transfers.domain.account.Account
import com.krzykrucz.transfers.domain.account.AccountIdentifier
import com.krzykrucz.transfers.domain.account.AccountNumber
import com.krzykrucz.transfers.domain.common.DomainEventPublisher
import org.joda.money.Money
import spock.lang.Specification

import static org.joda.money.CurrencyUnit.USD

// TODO try testing with AppServiceImpl as a root
class InMemoryAccountRepositoryTest extends Specification {

    final static TEN_USD = Money.of(USD, 10)

    def eventPublisher = Stub(DomainEventPublisher)

    def repository = new InMemoryAccountRepository()

    def "should modify account"() {
        given:
        def accountNumber = new AccountNumber("01")
        def account = new Account(AccountIdentifier.generate(), accountNumber, USD)
        repository.save(account)

        def accountFromRepository = repository.findByAccountNumber(accountNumber)
        accountFromRepository.depositMoney(TEN_USD)
        when:
        repository.save(accountFromRepository)

        then:
        repository.findByAccountNumber(accountNumber)
                .balance == TEN_USD
    }

    def "should not allow concurrent modification"() {
        given:
        def accountNumber = new AccountNumber("01")
        def account = new Account(AccountIdentifier.generate(), accountNumber, USD)
        repository.save(account)

        when:
        def accountFromRepository1 = repository.findByAccountNumber(accountNumber)
        def accountFromRepository2 = repository.findByAccountNumber(accountNumber)

        then:
        accountFromRepository1.version == 1
        accountFromRepository2.version == 1

        when:
        accountFromRepository1.depositMoney(TEN_USD)
        accountFromRepository2.depositMoney(TEN_USD)

        and:
        repository.save(accountFromRepository1)

        then:
        accountFromRepository1.version == 2

        when:
        repository.save(accountFromRepository2)

        then:
        thrown OptimisticLockException
    }

    def "should not provide same account object"() {
        given:
        def accountNumber = new AccountNumber("01")
        def account = new Account(AccountIdentifier.generate(), accountNumber, USD)
        repository.save(account)

        when:
        def firstFetch = repository.findByAccountNumber(accountNumber)
        def secondFetch = repository.findByAccountNumber(accountNumber)

        then:
        !firstFetch.is(secondFetch)
    }


}
