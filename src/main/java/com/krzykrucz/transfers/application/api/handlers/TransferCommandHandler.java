package com.krzykrucz.transfers.application.api.handlers;

import com.krzykrucz.transfers.application.TransfersApplicationService;
import com.krzykrucz.transfers.application.api.command.MoneyTransferCommand;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransferCommandHandler implements Handler {

    private final TransfersApplicationService transfersApplicationService;

    @Inject
    public TransferCommandHandler(TransfersApplicationService transfersApplicationService) {
        this.transfersApplicationService = transfersApplicationService;
    }

    @Override
    public void handle(Context ctx) {
        ctx.parse(MoneyTransferCommand.class)
                .then(moneyTransferCommand -> {
                    transfersApplicationService.transfer(moneyTransferCommand);
                    ctx.getResponse().status(Status.OK).send();
                });
    }

}
