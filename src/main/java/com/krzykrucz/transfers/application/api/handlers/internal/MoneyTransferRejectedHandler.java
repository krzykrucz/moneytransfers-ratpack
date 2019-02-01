package com.krzykrucz.transfers.application.api.handlers.internal;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.krzykrucz.transfers.application.TransfersApplicationService;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferRejected;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MoneyTransferRejectedHandler implements DomainEventHandler<MoneyTransferRejected> {

    private final TransfersApplicationService transfersApplicationService;

    @Inject
    public MoneyTransferRejectedHandler(TransfersApplicationService transfersApplicationService) {
        this.transfersApplicationService = transfersApplicationService;
    }

    @Override
    @Subscribe
    @AllowConcurrentEvents
    public void handle(MoneyTransferRejected event) {
        transfersApplicationService.rejectTransfer(event.getReferenceNumber());
    }

}
