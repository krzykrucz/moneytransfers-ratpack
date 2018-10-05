package com.krzykrucz.transfers

import com.krzykrucz.transfers.MoneyTransfersApplication
import com.krzykrucz.transfers.infrastructure.ExternalCurrencyExchanger
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.joda.money.Money
import ratpack.http.client.RequestSpec
import ratpack.test.MainClassApplicationUnderTest
import spock.lang.Specification

import static org.joda.money.CurrencyUnit.EUR
import static org.joda.money.CurrencyUnit.USD

class TransfersEndToEndTest extends Specification {

    final def TEN_DOLLARS = Money.of USD, 10
    final def THIRTY_DOLLARS = Money.of USD, 30
    final def THIRTY_EURO = Money.of EUR, 30

    def jsonParser = new JsonSlurper()

    def httpResponseCodes = []

    MainClassApplicationUnderTest app = new TestApplicationWithMockedServices(MoneyTransfersApplication)

    def cleanup() {
        app.close()
    }

    def "should transfer money"() {
        when:
        'account created' '01', 'USD'
        'account created' '02', 'USD'

        then:
        'balance of account'('01') == '$0.00'
        'balance of account'('02') == '$0.00'

        when:
        money THIRTY_DOLLARS 'deposited on account' '01'
        money THIRTY_DOLLARS 'deposited on account' '02'

        then:
        'balance of account'('01') == '$30.00'
        'balance of account'('02') == '$30.00'

        when:
        money(TEN_DOLLARS).transfered('01', '02')

        then:
        'balance of account'('01') == '$20.00'
        'balance of account'('02') == '$40.00'
        and:
        'all responses are' 200
    }

    def "should return 400 for non-existent account"() {
        when:
        money THIRTY_DOLLARS 'deposited on account' '01'

        then:
        'all responses are' 400
    }

    def "should return 500 for unknown error"() {
        given:
        'mocked currency exchanger' ThrowingExternalCurrencyExchanger.withExceptionText('error')
        'account created' '01', 'USD'

        when:
        def response = money THIRTY_EURO 'deposited on account' '01'

        then:
        response.statusCode == 500
        response.body.text == 'error'
    }

    def "account created"(number, currency) {
        post('account', [accountNumber: number, currencyCode: currency])
    }

    def "all responses are"(code) {
        def allMatch = httpResponseCodes.stream().allMatch { it == code }
        httpResponseCodes.clear()
        allMatch
    }

    def "balance of account"(number) {
        def textJSON = get("account/${number}").body.text
        def account = jsonParser.parseText(textJSON)
        account.balance.pretty
    }

    def "mocked currency exchanger"(ExternalCurrencyExchanger exchanger) {
        app = new TestApplicationWithMockedServices(MoneyTransfersApplication, exchanger)
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
            post('transfer', [value: [amount: money.amount, currency: money.currencyUnit.code], from: from, to: to])
        }

        def "deposited on account"(number) {
            post('deposit', [value: [amount: money.amount, currency: money.currencyUnit.code], accountNumber: number])
        }
    }

    def post(path, json) {
        def client = app.httpClient
        client.requestSpec { RequestSpec requestSpec ->
            requestSpec.body.type("application/json")
            requestSpec.body.text(JsonOutput.toJson(json))
        }
        def response = client.post(path)
        httpResponseCodes.add response.statusCode
        response
    }

    def get(path) {
        def client = app.httpClient
        def response = client.get(path)
        httpResponseCodes.add response.statusCode
        response
    }


}
