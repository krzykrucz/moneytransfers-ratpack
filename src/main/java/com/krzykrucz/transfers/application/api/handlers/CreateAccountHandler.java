package com.krzykrucz.transfers.application.api.handlers;

import com.krzykrucz.transfers.application.TransfersApplicationService;
import com.krzykrucz.transfers.application.api.command.OpenAccountCommand;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

import javax.inject.Inject;
import javax.inject.Singleton;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class CreateAccountHandler implements Handler {

    private final TransfersApplicationService transfersApplicationService;

    @Inject
    public CreateAccountHandler(TransfersApplicationService transfersApplicationService) {
        this.transfersApplicationService = transfersApplicationService;
    }

    @Override
    public void handle(Context ctx) {
        ctx.parse(fromJson(OpenAccountCommand.class))
                .then(moneyTransferCommand -> {
                    transfersApplicationService.openAccount(moneyTransferCommand);
                    ctx.getResponse().status(Status.OK).send();
                });
    }
}
