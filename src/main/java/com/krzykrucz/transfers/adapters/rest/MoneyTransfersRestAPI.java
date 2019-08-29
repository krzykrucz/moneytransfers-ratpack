package com.krzykrucz.transfers.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.krzykrucz.transfers.adapters.rest.error.ExceptionHandler;
import com.krzykrucz.transfers.adapters.rest.handlers.CreateAccountHandler;
import com.krzykrucz.transfers.adapters.rest.handlers.DepositMoneyHandler;
import com.krzykrucz.transfers.adapters.rest.handlers.GetAccountHandler;
import com.krzykrucz.transfers.adapters.rest.handlers.TransferCommandHandler;
import com.krzykrucz.transfers.adapters.rest.jackson.MoneyDeserializer;
import com.krzykrucz.transfers.adapters.rest.jackson.MoneySerializer;
import org.joda.money.Money;
import ratpack.error.ServerErrorHandler;
import ratpack.func.Action;
import ratpack.handling.Chain;


public class MoneyTransfersRestAPI implements Action<Chain> {

    @Override
    public void execute(Chain chain) throws Exception {
        chain
                .register(r -> r.add(ServerErrorHandler.class, new ExceptionHandler()))
                .register(r -> r.add(
                        ObjectMapper.class, new ObjectMapper().registerModule(new MoneySerializationModule())
                ))
                .post("transfer/perform", TransferCommandHandler.class)
                .post("account", CreateAccountHandler.class)
                .get("account/:number", GetAccountHandler.class)
                .post("deposit", DepositMoneyHandler.class)
                .all(ctx -> ctx.render("Root handler"));
    }

    private class MoneySerializationModule extends SimpleModule {
        MoneySerializationModule() {
            super.addSerializer(Money.class, new MoneySerializer());
            super.addDeserializer(Money.class, new MoneyDeserializer());
        }
    }

}
