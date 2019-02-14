package com.krzykrucz.transfers.domain.util

import com.krzykrucz.transfers.application.DomainAPI
import com.krzykrucz.transfers.application.DomainFacade
import com.krzykrucz.transfers.application.api.command.DepositMoneyCommand
import com.krzykrucz.transfers.application.api.command.OpenAccountCommand
import com.krzykrucz.transfers.application.api.command.PerformMoneyTransferCommand
import com.krzykrucz.transfers.application.api.handlers.internal.MoneyTransferAcceptedHandler
import com.krzykrucz.transfers.application.api.handlers.internal.MoneyTransferCommissionedHandler
import com.krzykrucz.transfers.application.api.handlers.internal.MoneyTransferRejectedHandler
import com.krzykrucz.transfers.domain.account.AccountNumber
import com.krzykrucz.transfers.infrastructure.exchanger.IdentityCurrencyExchanger
import org.joda.money.Money
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import static org.joda.money.CurrencyUnit.EUR
import static org.joda.money.CurrencyUnit.USD

class DomainTest extends Specification {

    final def TEN_DOLLARS = Money.of USD, 10
    final def TEN_EURO = Money.of EUR, 10
    final def THIRTY_DOLLARS = Money.of USD, 30
    final def USD_30_000 = Money.of USD, 30000
    final def USD_15_000 = Money.of USD, 15000
    final def USD_15_001 = Money.of USD, 15001
    final def THIRTY_EURO = Money.of EUR, 30

    @Shared
    def conditions = new PollingConditions(timeout: 5, factor: 1.25)

    @Shared
    DomainAPI domainAPI

    @Shared
    InMemoryAccountRepositoryInTest repository

    @Shared
    EventPublisherSpy eventPublisher


    def setupSpec() {
        def exchanger = new IdentityCurrencyExchanger()
        eventPublisher = new EventPublisherSpy()
        repository = new InMemoryAccountRepositoryInTest(eventPublisher)
        domainAPI = new DomainFacade(repository, exchanger)

        def applicationService = new AppServiceStub(domainAPI)
        def moneyTransferAcceptedHandler = Spy(new MoneyTransferAcceptedHandler(applicationService))
        def moneyTransferCommissionedHandler = Spy(new MoneyTransferCommissionedHandler(applicationService))
        def moneyTransferRejectedHandler = Spy(new MoneyTransferRejectedHandler(applicationService))
        eventPublisher.subcribe((Set) [
                moneyTransferAcceptedHandler,
                moneyTransferCommissionedHandler,
                moneyTransferRejectedHandler
        ])
    }

    def cleanup() {
        repository.clear()
        eventPublisher.reset()
    }

    def 'transfer accepted event published'() {
        eventPublisher.checkTransferAcceptedEventReceived()
    }

    def "transfer rejected event published"() {
        eventPublisher.checkTransferRejectedEventReceived()
    }

    def "account created"(number, currency) {
        domainAPI.openAccount(new OpenAccountCommand(number, currency))
    }

    def "balance of account"(number) {
        def balance = repository.findByAccountNumber(new AccountNumber(number)).balance
        "${balance.currencyUnit.symbol}${balance.amount.toPlainString()}"
    }

    CommandBuilder money(Money money) {
        new CommandBuilder(money)
    }

    private class CommandBuilder {
        private Money money

        CommandBuilder(Money money) {
            this.money = money
        }

        def transfered(from, to) {
            domainAPI.transfer(new PerformMoneyTransferCommand(from, to, money))
        }

        def "deposited on account"(number) {
            domainAPI.depositMoney(new DepositMoneyCommand(money, number))
        }
    }

}
