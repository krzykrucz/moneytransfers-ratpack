package com.krzykrucz.transfers.application.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.joda.money.Money
import spock.lang.Specification

import static org.joda.money.CurrencyUnit.USD

class MoneySerializationTest extends Specification {

    def serializer = new MoneySerializer()
    def deserializer = new MoneyDeserializer()
    def mapper = new ObjectMapper()

    def setup() {
        mapper.registerModule(new SimpleModule()
                .addSerializer(Money, serializer)
                .addDeserializer(Money, deserializer))
    }

    def "should serialize and deserialize to the same object"() {
        given:
        def USD_10_50 = Money.of USD, 10.50

        when:
        def serializedMoney = mapper.writeValueAsString(USD_10_50)
        def deserializedMoney = mapper.readValue(serializedMoney, Money)

        then:
        deserializedMoney == USD_10_50
    }

}
