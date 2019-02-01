package com.krzykrucz.transfers.application.api.handlers.internal;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.krzykrucz.transfers.application.TransfersApplicationService;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferAccepted;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MoneyTransferAcceptedHandler implements DomainEventHandler<MoneyTransferAccepted> {

    private final TransfersApplicationService transfersApplicationService;

    @Inject
    public MoneyTransferAcceptedHandler(TransfersApplicationService transfersApplicationService) {
        this.transfersApplicationService = transfersApplicationService;
    }

    @Override
    @Subscribe
    @AllowConcurrentEvents
    public void handle(MoneyTransferAccepted event) {
        transfersApplicationService.acceptTransfer(event.getTransferReferenceNumber());
    }

}
