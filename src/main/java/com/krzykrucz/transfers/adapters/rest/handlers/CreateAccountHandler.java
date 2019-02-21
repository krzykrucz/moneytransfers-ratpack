package com.krzykrucz.transfers.adapters.rest.handlers;

import com.krzykrucz.transfers.application.ResilientAccountApplicationService;
import com.krzykrucz.transfers.application.api.command.OpenAccountCommand;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

import javax.inject.Inject;
import javax.inject.Singleton;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class CreateAccountHandler implements Handler {

    private final ResilientAccountApplicationService accountApplicationService;

    @Inject
    public CreateAccountHandler(ResilientAccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @Override
    public void handle(Context ctx) {
        ctx.parse(fromJson(OpenAccountCommand.class))
                .then(moneyTransferCommand -> {
                    accountApplicationService.openAccount(moneyTransferCommand);
                    ctx.getResponse().status(Status.OK).send();
                });
    }
}
