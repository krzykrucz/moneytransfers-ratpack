package com.krzykrucz.transfers.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.krzykrucz.transfers.infrastructure.ExternalCurrencyExchanger
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import spock.lang.Specification

import static spock.util.matcher.HamcrestMatchers.closeTo
import static spock.util.matcher.HamcrestSupport.that

class ExternalCurrencyExchangerTest extends Specification {

    def "should parse external currency rates"() {
        given:
        def json = '{"base":"USD","rates":{"CAD":1.3069958145,"DKK":6.3714871444,"AUD":1.3837874776,"EUR":0.854189801},"date":"2018-09-27"}'
        def client = createClientReturning(json)
        def exchanger = new ExternalCurrencyExchanger(client, new ObjectMapper())

        when:
        def exchanged = exchanger.exchange(Money.of(CurrencyUnit.USD, 10), CurrencyUnit.EUR)

        then:
        that exchanged.amount, closeTo(8.54, 0.1)
    }

    def createClientReturning(String json) {
        def client = Stub(HttpClient)
        def response = Stub(HttpResponse)
        client.execute(_) >> response
        def entity = Stub(HttpEntity)
        response.entity >> entity
        entity.content >> new ByteArrayInputStream(json.bytes)
        client
    }

}
