package com.krzykrucz.transfers.domain.util

import com.krzykrucz.transfers.adapters.events.GuavaDomainEventPublisher
import com.krzykrucz.transfers.adapters.exchanger.IdentityCurrencyExchanger
import com.krzykrucz.transfers.application.AccountApplicationService
import com.krzykrucz.transfers.application.AccountApplicationServiceImpl
import com.krzykrucz.transfers.application.api.command.*
import com.krzykrucz.transfers.domain.CurrencyExchanger
import com.krzykrucz.transfers.domain.account.AccountNumber
import com.krzykrucz.transfers.domain.account.MoneyTransfer
import com.krzykrucz.transfers.domain.common.DomainEventPublisher
import org.joda.money.Money
import spock.lang.Shared
import spock.lang.Specification

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
    AccountApplicationService service

    @Shared
    InMemoryAccountRepositoryInTest repository

    @Shared
    DomainEventPublisher eventPublisher

    @Shared
    CurrencyExchanger exchanger

    def setupSpec() {
        exchanger = new IdentityCurrencyExchanger()
        eventPublisher = new GuavaDomainEventPublisher()
        repository = new InMemoryAccountRepositoryInTest()
        service = new AccountApplicationServiceImpl(repository, exchanger, eventPublisher)
    }

    def cleanup() {
        repository.clear()
    }

    def "account created"(number, currency) {
        service.openAccount(new OpenAccountCommand(number, currency))
    }

    def "balance of account"(number) {
        def balance = repository.findByAccountNumber(new AccountNumber(number)).balance
        "${balance.currencyUnit.symbol}${balance.amount.toPlainString()}"
    }

    MoneyCommandBuilder money(Money money) {
        new MoneyCommandBuilder(money)
    }

    private class MoneyCommandBuilder {
        private Money money

        MoneyCommandBuilder(Money money) {
            this.money = money
        }

        def transfered(from, to) {
            service.transfer(new PerformMoneyTransferCommand(from, to, money))
        }

        def "deposited on account"(number) {
            service.depositMoney(new DepositMoneyCommand(money, number))
        }
    }

    TransferCommandBuilder 'last commissioned transfer'() {
        new TransferCommandBuilder(eventPublisher.lastCommissionedTransfer)
    }

    private class TransferCommandBuilder {
        private MoneyTransfer transfer

        TransferCommandBuilder(MoneyTransfer transfer) {
            this.transfer = transfer
        }

        def "received"() {
            service.receiveTransfer(new ReceiveTransferCommand(transfer))
        }

        def "rejected at sender"() {
            service.rejectTransfer(new RejectTransferCommand(transfer.referenceNumber))
        }

        def "accepted at sender"() {
            service.acceptTransfer(new AcceptTransferCommand(transfer.referenceNumber))
        }
    }

}
