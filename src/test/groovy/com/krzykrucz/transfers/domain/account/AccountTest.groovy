package com.krzykrucz.transfers.domain.account

import com.krzykrucz.transfers.domain.account.event.MoneyTransferAccepted
import com.krzykrucz.transfers.domain.account.event.MoneyTransferCommissioned
import com.krzykrucz.transfers.domain.account.event.MoneyTransferRejected
import org.joda.money.Money
import spock.lang.Specification

import static com.krzykrucz.transfers.domain.account.AccountIdentifier.generate
import static org.joda.money.CurrencyUnit.EUR
import static org.joda.money.CurrencyUnit.USD

class AccountTest extends Specification {

    final def ACCOUNT_2 = new AccountNumber("22")
    final def TEN_DOLLARS = Money.of(USD, 10)
    final def TWENTY_DOLLARS = Money.of(USD, 20)

    def "should create empty account"() {
        when:
        def account = newUSDAccount()

        then:
        account.balance == Money.zero(USD)
    }

    def "should deposit money"() {
        given:
        def account = newUSDAccount()

        when:
        account.depositMoney TWENTY_DOLLARS

        then:
        account.balance == TWENTY_DOLLARS
    }

    def "should commission transfer"() {
        given:
        def account = newUSDAccount()
        account.depositMoney TWENTY_DOLLARS

        when:
        account.commissionTransferTo ACCOUNT_2, TEN_DOLLARS

        then:
        account.pendingTransfers.size() == 1
        account.balance == TEN_DOLLARS

        def pendingTransfer = account.pendingTransfers.head()
        def expectedTransfer = new MoneyTransfer(pendingTransfer, TEN_DOLLARS, account.number, ACCOUNT_2)
        def expectedBlockedMoney = TEN_DOLLARS
        def events = account.eventsAndFlush.asJava()

        events*.class == [MoneyTransferCommissioned]
        events*.moneyTransfer == [expectedTransfer]
        events*.blockedMoney == [expectedBlockedMoney]
    }

    def "test confirm outcoming transfer"() {
        given:
        def account = newUSDAccount()
        account.depositMoney TWENTY_DOLLARS
        account.commissionTransferTo ACCOUNT_2, TEN_DOLLARS

        when:
        def pendingTransferRefNumber = account.pendingTransfers.head()
        account.confirmTransfer pendingTransferRefNumber

        then:
        account.pendingTransfers.size() == 0
        account.balance == TEN_DOLLARS
    }

    def "should reject outcoming transfer"() {
        given:
        def account = newUSDAccount()
        account.depositMoney TWENTY_DOLLARS
        account.commissionTransferTo ACCOUNT_2, TEN_DOLLARS

        when:
        def pendingTransferRefNumber = account.pendingTransfers.head()
        account.rejectTransfer pendingTransferRefNumber

        then:
        account.pendingTransfers.size() == 0
        account.balance == TWENTY_DOLLARS
    }


    def "should accept incoming transfer"() {
        given:
        def account = newUSDAccount()

        when:
        def transfer = MoneyTransfer.generateMoneyTransfer(TEN_DOLLARS, ACCOUNT_2, account.number)
        account.receiveTransfer(transfer)

        then:
        account.pendingTransfers.size() == 0
        account.balance == TEN_DOLLARS

        def events = account.eventsAndFlush.asJava()

        events*.class == [MoneyTransferAccepted]
        events.head() != null
    }


    def "should reject incoming transfer"() {
        given:
        def account = newUSDAccount()
        def dollarsBeyondLimit = Money.of(USD, 20000)

        when:
        def transfer = MoneyTransfer.generateMoneyTransfer(dollarsBeyondLimit, ACCOUNT_2, account.number)
        account.receiveTransfer(transfer)

        then:
        account.pendingTransfers.size() == 0
        account.balance == Money.zero(USD)

        def events = account.eventsAndFlush.asJava()

        events*.class == [MoneyTransferRejected]
        events.head() != null
    }

    def "should reject transfer with a different currency"() {
        given:
        def usdAccount = newUSDAccount()
        def tenEuro = Money.of(EUR, 10)

        when:
        def eurTransfer = MoneyTransfer.generateMoneyTransfer(tenEuro, ACCOUNT_2, usdAccount.number)
        usdAccount.receiveTransfer(eurTransfer)

        then:
        thrown IllegalStateException
    }

    def "should reject deposit with a different currency"() {
        given:
        def usdAccount = newUSDAccount()
        def tenEuro = Money.of(EUR, 10)

        when:
        usdAccount.depositMoney(tenEuro)

        then:
        thrown IllegalStateException
    }

    def "should reject commissioning transfer with a different currency"() {
        given:
        def usdAccount = newUSDAccount()
        usdAccount.depositMoney(TWENTY_DOLLARS)
        def tenEuro = Money.of(EUR, 10)

        when:
        usdAccount.commissionTransferTo(ACCOUNT_2, tenEuro)

        then:
        thrown IllegalStateException
    }

    def newUSDAccount(AccountIdentifier id = generate(), AccountNumber accountNumber = new AccountNumber("01")) {
        new Account(id, accountNumber, USD)
    }

}
