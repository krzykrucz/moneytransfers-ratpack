package com.krzykrucz.transfers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Lists;
import com.krzykrucz.transfers.application.api.command.DepositMoneyCommand;
import com.krzykrucz.transfers.application.api.command.MoneyTransferCommand;
import com.krzykrucz.transfers.application.api.command.OpenAccountCommand;
import com.krzykrucz.transfers.application.jackson.MoneySerializer;
import com.krzykrucz.transfers.infrastructure.ExternalCurrencyExchanger;
import groovy.json.JsonSlurper;
import groovy.json.internal.LazyMap;
import org.joda.money.Money;
import org.junit.After;
import org.junit.Test;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.MainClassApplicationUnderTest;
import ratpack.test.http.TestHttpClient;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.joda.money.CurrencyUnit.EUR;
import static org.joda.money.CurrencyUnit.USD;

public class JUnitTransfersEndToEndTest {

    private final Money TEN_DOLLARS = Money.of(USD, 10);
    private final Money THIRTY_DOLLARS = Money.of(USD, 30);
    private final Money THIRTY_EURO = Money.of(EUR, 30);

    private JsonSlurper jsonParser = new JsonSlurper();

    private List<Integer> httpResponseCodes = Lists.newArrayList();

    private MainClassApplicationUnderTest app = new TestApplicationWithMockedServices(MoneyTransfersApplication.class);

    @After
    public void cleanup() {
        app.close();
    }

    @Test
    public void shouldTransferMoney() throws IllegalAccessException {
        // when:
        accountCreated("01", "USD");
        accountCreated("02", "USD");

        // then:
        assertThat(balanceOfAccount("01"), equalTo("$0.00"));
        assertThat(balanceOfAccount("02"), equalTo("$0.00"));

        // when:
        money(THIRTY_DOLLARS).depositedOnAccount("01");
        money(THIRTY_DOLLARS).depositedOnAccount("02");

        // then:
        assertThat(balanceOfAccount("01"), equalTo("$30.00"));
        assertThat(balanceOfAccount("02"), equalTo("$30.00"));

        // when:
        money(TEN_DOLLARS).transfered("01", "02");

        // then:
        assertThat(balanceOfAccount("01"), equalTo("$20.00"));
        assertThat(balanceOfAccount("02"), equalTo("$40.00"));

        // and:
        allResponsesAre(200);
    }

    @Test
    public void shouldReturn400ForNonExistentAccount() {
        // when:
        money(THIRTY_DOLLARS).depositedOnAccount("01");

        // then:
        allResponsesAre(400);
    }

    @Test
    public void shouldReturn500ForUnknownError() {
        // given:
        mockedCurrencyExchanger(ThrowingExternalCurrencyExchanger.withExceptionText("error"));
        accountCreated("01", "USD");

        // when:
        ReceivedResponse response = money(THIRTY_EURO).depositedOnAccount("01");

        // then:
        assertThat(response.getStatusCode(), equalTo(500));
        assertThat(response.getBody().getText(), equalTo("error"));
    }

    private void accountCreated(String number, String currency) {
        post("account", new OpenAccountCommand(number, currency));
    }

    private void allResponsesAre(int code) {
        boolean allMatch = httpResponseCodes.stream()
                .allMatch(responseCode -> responseCode.equals(code));
        httpResponseCodes.clear();
        assertTrue(allMatch);
    }

    private String balanceOfAccount(String number) throws IllegalAccessException {
        String textJSON = get("account/" + number).getBody().getText();
        Object account = jsonParser.parseText(textJSON);

        LazyMap balance = (LazyMap) ((LazyMap) account).get("balance");
        return (String) balance.get("pretty");
    }

    private void mockedCurrencyExchanger(ExternalCurrencyExchanger exchanger) {
        app = new TestApplicationWithMockedServices(MoneyTransfersApplication.class, exchanger);
    }

    private CommandBuilder money(Money money) {
        return new CommandBuilder(money);
    }

    private class CommandBuilder {
        private Money money;

        CommandBuilder(Money money) {
            this.money = money;
        }

        ReceivedResponse transfered(String from, String to) {
            return post("transfer", new MoneyTransferCommand(from, to, money));
        }

        ReceivedResponse depositedOnAccount(String number) {
            return post("deposit", new DepositMoneyCommand(money, number));
        }
    }

    private ReceivedResponse post(String path, Object json) {
        TestHttpClient client = app.getHttpClient();
        client.requestSpec(requestSpec -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
            mapper.registerModule(new SimpleModule().addSerializer(Money.class, new MoneySerializer()));
            final String text = mapper.writeValueAsString(json);
            requestSpec.getBody().type("application/json").text(text);
        });
        ReceivedResponse response = client.post(path);
        httpResponseCodes.add(response.getStatusCode());
        return response;
    }

    private ReceivedResponse get(String path) {
        TestHttpClient client = app.getHttpClient();
        ReceivedResponse response = client.get(path);
        httpResponseCodes.add(response.getStatusCode());
        return response;
    }


}
