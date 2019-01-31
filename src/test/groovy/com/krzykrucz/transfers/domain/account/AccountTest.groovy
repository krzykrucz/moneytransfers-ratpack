package com.krzykrucz.transfers.domain.account

import com.krzykrucz.transfers.domain.CurrencyExchanger
import com.krzykrucz.transfers.domain.account.event.MoneyTransferAccepted
import com.krzykrucz.transfers.domain.account.event.MoneyTransferCommissioned
import com.krzykrucz.transfers.domain.account.event.MoneyTransferRejected
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import spock.lang.Specification

import static com.krzykrucz.transfers.domain.account.AccountIdentifier.generate
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
        account.pendingTransfersReferenceNumbers.size() == 1
        account.balance == TEN_DOLLARS

        def pendingTransfer = account.pendingTransfersReferenceNumbers.head()
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
        def pendingTransferRefNumber = account.pendingTransfersReferenceNumbers.head()
        account.confirmTransfer pendingTransferRefNumber

        then:
        account.pendingTransfersReferenceNumbers.size() == 0
        account.balance == TEN_DOLLARS
    }

    def "should reject outcoming transfer"() {
        given:
        def account = newUSDAccount()
        account.depositMoney TWENTY_DOLLARS
        account.commissionTransferTo ACCOUNT_2, TEN_DOLLARS

        when:
        def pendingTransferRefNumber = account.pendingTransfersReferenceNumbers.head()
        account.rejectTransfer pendingTransferRefNumber

        then:
        account.pendingTransfersReferenceNumbers.size() == 0
        account.balance == TWENTY_DOLLARS
    }


    def "should accept incoming transfer"() {
        given:
        def account = newUSDAccount()

        when:
        def transfer = MoneyTransfer.generateMoneyTransfer(TEN_DOLLARS, ACCOUNT_2, account.number)
        account.receiveTransfer(transfer)

        then:
        account.pendingTransfersReferenceNumbers.size() == 0
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
        account.pendingTransfersReferenceNumbers.size() == 0
        account.balance == Money.zero(USD)

        def events = account.eventsAndFlush.asJava()

        events*.class == [MoneyTransferRejected]
        events.head() != null
    }


    def newUSDAccount(AccountIdentifier id = generate(), AccountNumber accountNumber = new AccountNumber("01"),
                      CurrencyExchanger currencyExchanger = new IdentityCurrencyExchanger()) {
        new Account(id, accountNumber, USD, currencyExchanger)
    }

    class IdentityCurrencyExchanger implements CurrencyExchanger {

        @Override
        Money exchange(Money money, CurrencyUnit currencyUnit) {
            return Money.of(currencyUnit, money.amount)
        }
    }

}
