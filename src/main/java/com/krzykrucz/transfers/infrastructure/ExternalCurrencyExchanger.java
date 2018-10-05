package com.krzykrucz.transfers.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krzykrucz.transfers.domain.CurrencyExchanger;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class ExternalCurrencyExchanger implements CurrencyExchanger {

    private static final String EXTERNAL_EXCHANGE_RATE_API_URL = "https://api.exchangeratesapi.io/latest?base=%s";

    private final HttpClient httpClient;

    private final ObjectMapper jsonMapper;

    public ExternalCurrencyExchanger(HttpClient httpClient, ObjectMapper jsonMapper) {
        this.httpClient = httpClient;
        this.jsonMapper = jsonMapper;
    }

    public ExternalCurrencyExchanger() {
        this(HttpClientBuilder.create().build(), new ObjectMapper());
    }

    @Override
    public Money exchange(Money money, CurrencyUnit targetCurrencyUnit) {
        final CurrencyUnit baseCurrency = money.getCurrencyUnit();
        try {
            final HttpResponse response = httpClient.execute(new HttpGet(String.format(EXTERNAL_EXCHANGE_RATE_API_URL, baseCurrency.getCode())));
            final ExternalRates externalRates = jsonMapper.readValue(response.getEntity().getContent(), ExternalRates.class);
            final BigDecimal rate = new BigDecimal(externalRates.getRates().get(targetCurrencyUnit.getCode()));
            final BigDecimal convertedValue = money.getAmount().multiply(rate).setScale(2, RoundingMode.DOWN);
            return Money.of(targetCurrencyUnit, convertedValue);

        } catch (IOException e) {
            throw new IllegalStateException("Cannot retrieve exchange rates", e);
        }
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ExternalRates {

        private Map<String, String> rates;

    }
}
