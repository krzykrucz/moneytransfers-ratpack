package com.krzykrucz.transfers.application.api.handlers.internal;

import com.krzykrucz.transfers.application.TransfersApplicationService;
import com.krzykrucz.transfers.domain.DomainEvent;
import com.krzykrucz.transfers.domain.DomainEventPublisher;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferAccepted;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferCommissioned;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferRejected;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Singleton
public class TransferInternalEventHandler implements DomainEventHandler {

    private final TransfersApplicationService transfersApplicationService;

    @Inject
    public TransferInternalEventHandler(DomainEventPublisher domainEventPublisher, TransfersApplicationService transfersApplicationService) {
        this.transfersApplicationService = transfersApplicationService;
        domainEventPublisher.subscribe(this);
    }

    @Override
    public void handle(DomainEvent domainEvent) {
        Match(domainEvent).of(
                Case($(instanceOf(MoneyTransferAccepted.class)), event -> runAsync(() -> acceptTransfer(event))),
                Case($(instanceOf(MoneyTransferRejected.class)), event -> runAsync(() -> rejectTransfer(event))),
                Case($(instanceOf(MoneyTransferCommissioned.class)), event -> runAsync(() -> receiveTransfer(event))),
                Case($(), e -> e)
        );
    }

    private void receiveTransfer(MoneyTransferCommissioned event) {
        transfersApplicationService.receiveTransfer(event.getMoneyTransfer());
    }

    private void rejectTransfer(MoneyTransferRejected domainEvent) {
        transfersApplicationService.rejectTransfer(domainEvent.getReferenceNumber());
    }

    private void acceptTransfer(MoneyTransferAccepted domainEvent) {
        transfersApplicationService.acceptTransfer(domainEvent.getTransferReferenceNumber());
    }

    // TODO review thoroughly
    private Void runAsync(Runnable runnable) {
        runnable.run();
        return null;
    }
}
