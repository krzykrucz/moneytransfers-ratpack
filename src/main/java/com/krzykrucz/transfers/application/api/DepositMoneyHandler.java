package com.krzykrucz.transfers.application.api;

import com.google.inject.Singleton;
import com.krzykrucz.transfers.application.TransfersApplicationService;
import com.krzykrucz.transfers.application.api.command.DepositMoneyCommand;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

import javax.inject.Inject;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class DepositMoneyHandler implements Handler {

    private final TransfersApplicationService transfersApplicationService;

    @Inject
    public DepositMoneyHandler(TransfersApplicationService transfersApplicationService) {
        this.transfersApplicationService = transfersApplicationService;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        ctx.parse(fromJson(DepositMoneyCommand.class))
                .then(moneyTransferCommand -> {
                    transfersApplicationService.depositMoney(moneyTransferCommand);
                    ctx.getResponse().status(Status.OK).send();
                });
    }
}
