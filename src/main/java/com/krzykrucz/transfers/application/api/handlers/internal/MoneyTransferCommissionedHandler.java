package com.krzykrucz.transfers.application.api.handlers.internal;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.krzykrucz.transfers.application.TransfersApplicationService;
import com.krzykrucz.transfers.application.api.command.ReceiveTransferCommand;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferCommissioned;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MoneyTransferCommissionedHandler implements DomainEventHandler<MoneyTransferCommissioned> {

    private final TransfersApplicationService transfersApplicationService;

    @Inject
    public MoneyTransferCommissionedHandler(TransfersApplicationService transfersApplicationService) {
        this.transfersApplicationService = transfersApplicationService;
    }

    @Override
    @Subscribe
    @AllowConcurrentEvents
    public void handle(MoneyTransferCommissioned event) {
        transfersApplicationService.receiveTransfer(new ReceiveTransferCommand(event.getMoneyTransfer()));
    }

}
