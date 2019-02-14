package com.krzykrucz.transfers.application.api;

import com.krzykrucz.transfers.application.api.handlers.CreateAccountHandler;
import com.krzykrucz.transfers.application.api.handlers.DepositMoneyHandler;
import com.krzykrucz.transfers.application.api.handlers.GetAccountHandler;
import com.krzykrucz.transfers.application.api.handlers.TransferCommandHandler;
import com.krzykrucz.transfers.application.error.ExceptionHandler;
import ratpack.error.ServerErrorHandler;
import ratpack.func.Action;
import ratpack.handling.Chain;


public class MoneyTransfersRestAPI implements Action<Chain> {

    @Override
    public void execute(Chain chain) throws Exception {
        chain // TODO add rejecting, accepting, receiving transfers
                .register(r -> r.add(ServerErrorHandler.class, new ExceptionHandler()))
                .post("transfer/perform", TransferCommandHandler.class)
                .post("account", CreateAccountHandler.class)
                .get("account/:number", GetAccountHandler.class)
                .post("deposit", DepositMoneyHandler.class)
                .all(ctx -> ctx.render("Root handler"));
    }

}
