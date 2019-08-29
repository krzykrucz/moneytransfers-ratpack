package com.krzykrucz.transfers.adapters.rest.handlers;

import com.krzykrucz.transfers.application.ResilientAccountApplicationService;
import com.krzykrucz.transfers.application.api.command.PerformMoneyTransferCommand;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransferCommandHandler implements Handler {

    private final ResilientAccountApplicationService accountApplicationService;

    @Inject
    public TransferCommandHandler(ResilientAccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @Override
    public void handle(Context ctx) {
        ctx.parse(PerformMoneyTransferCommand.class)
                .then(moneyTransferCommand -> {
                    accountApplicationService.transfer(moneyTransferCommand);
                    ctx.getResponse().status(Status.OK).send();
                });
    }

}
