package com.krzykrucz.transfers.adapters.exchanger

import com.krzykrucz.transfers.domain.account.AccountNumber
import com.krzykrucz.transfers.domain.account.MoneyTransfer
import com.krzykrucz.transfers.domain.account.TransferReferenceNumber
import org.joda.money.Money
import spock.lang.Specification

import static org.joda.money.CurrencyUnit.EUR
import static org.joda.money.CurrencyUnit.USD

class IdentityCurrencyExchangerTest extends Specification {

    static final TRANSFER_REF = new TransferReferenceNumber(UUID.randomUUID())
    static final ACCOUNT_1 = new AccountNumber("01")
    static final ACCOUNT_2 = new AccountNumber("02")

    def currencyExchanger = new IdentityCurrencyExchanger()

    def "should change only currency"() {
        given:
        def tenDollars = Money.of(USD, 10)

        when:
        def exchanged = currencyExchanger.exchangeIfNecessary(tenDollars, EUR)

        then:
        exchanged.amount == 10
        exchanged.currencyUnit == EUR
    }

    def "should change only currency in money transfer"() {
        given:
        def tenDollars = Money.of(USD, 10)
        def transfer = new MoneyTransfer(TRANSFER_REF, tenDollars, ACCOUNT_1, ACCOUNT_2)

        when:
        def exchanged = currencyExchanger.exchangeIfNecessary(transfer, EUR)

        then:
        exchanged.value.amount == 10
        exchanged.value.currencyUnit == EUR
    }

}
