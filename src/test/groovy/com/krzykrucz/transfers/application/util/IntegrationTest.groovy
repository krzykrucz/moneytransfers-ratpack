package com.krzykrucz.transfers.application.util

import com.krzykrucz.transfers.MoneyTransfersApplication
import com.krzykrucz.transfers.domain.CurrencyExchanger
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.joda.money.Money
import ratpack.http.client.RequestSpec
import ratpack.test.MainClassApplicationUnderTest
import spock.lang.Specification

import static org.joda.money.CurrencyUnit.EUR
import static org.joda.money.CurrencyUnit.USD

@Slf4j
class IntegrationTest extends Specification {

    final def TEN_DOLLARS = Money.of USD, 10
    final def TEN_EURO = Money.of EUR, 10
    final def THIRTY_DOLLARS = Money.of USD, 30
    final def USD_15_000 = Money.of USD, 15000
    final def THIRTY_EURO = Money.of EUR, 30

    def jsonParser = new JsonSlurper()

    def httpResponseCodes = []

    MainClassApplicationUnderTest app = new TestApplicationWithMockedServices(MoneyTransfersApplication)

    def cleanup() {
        app.close()
    }

    def "account created"(number, currency) {
        post('account', [accountNumber: number, currencyCode: currency])
    }

    def "all responses are"(code) {
        def allMatch = httpResponseCodes.stream().allMatch { it == code }
        if (!allMatch) {
            log.error("Responses: ${httpResponseCodes}")
        }
        httpResponseCodes.clear()
        allMatch
    }

    def "balance of account"(number) {
        def textJSON = get("account/${number}").body.text
        def account = jsonParser.parseText(textJSON)
        account.balance.pretty
    }

    def "mocked currency exchanger"(CurrencyExchanger exchanger) {
        app = new TestApplicationWithMockedServices(MoneyTransfersApplication, exchanger)
    }

    CommandBuilder money(Money money = THIRTY_DOLLARS) {
        new CommandBuilder(money)
    }

    private class CommandBuilder {
        private Money money

        CommandBuilder(Money money) {
            this.money = money
        }

        def transfered(from, to) {
            post('transfer/perform', [value: [amount: money.amountMinor, currency: money.currencyUnit.code], from: from, to: to])
        }

        def "deposited on account"(number = '01') {
            post('deposit', [value: [amount: money.amountMinor, currency: money.currencyUnit.code], accountNumber: number])
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
