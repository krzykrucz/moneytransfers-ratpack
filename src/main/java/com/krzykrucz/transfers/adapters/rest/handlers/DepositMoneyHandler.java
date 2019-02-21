package com.krzykrucz.transfers.adapters.rest.handlers;

import com.google.inject.Singleton;
import com.krzykrucz.transfers.application.ResilientAccountApplicationService;
import com.krzykrucz.transfers.application.api.command.DepositMoneyCommand;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

import javax.inject.Inject;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class DepositMoneyHandler implements Handler {

    private final ResilientAccountApplicationService accountApplicationService;

    @Inject
    public DepositMoneyHandler(ResilientAccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @Override
    public void handle(Context ctx) {
        ctx.parse(fromJson(DepositMoneyCommand.class))
                .then(moneyTransferCommand -> {
                    accountApplicationService.depositMoney(moneyTransferCommand);
                    ctx.getResponse().status(Status.OK).send();
                });
    }
}
