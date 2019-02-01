package com.krzykrucz.transfers.application.api;

import com.krzykrucz.transfers.application.api.handlers.CreateAccountHandler;
import com.krzykrucz.transfers.application.api.handlers.DepositMoneyHandler;
import com.krzykrucz.transfers.application.api.handlers.GetAccountHandler;
import com.krzykrucz.transfers.application.api.handlers.TransferCommandHandler;
import com.krzykrucz.transfers.application.error.ExceptionHandler;
import ratpack.error.ServerErrorHandler;
import ratpack.func.Action;
import ratpack.handling.Chain;


public class MoneyTransfersAPI implements Action<Chain> {

    @Override
    public void execute(Chain chain) throws Exception {
        chain
                .register(r -> r.add(ServerErrorHandler.class, new ExceptionHandler()))
                .post("transfer", TransferCommandHandler.class)
                .post("account", CreateAccountHandler.class)
                .get("account/:number", GetAccountHandler.class)
                .post("deposit", DepositMoneyHandler.class)
                .all(ctx -> ctx.render("Root handler"));
    }

}
