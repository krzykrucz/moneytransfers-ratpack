package com.krzykrucz.transfers.application;

import com.krzykrucz.transfers.domain.DomainEvent;
import com.krzykrucz.transfers.domain.DomainEventPublisher;
import com.krzykrucz.transfers.domain.EventHandler;
import com.krzykrucz.transfers.domain.account.MoneyTransferAccepted;
import com.krzykrucz.transfers.domain.account.MoneyTransferCommissioned;
import com.krzykrucz.transfers.domain.account.MoneyTransferRejected;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Singleton
public class TransferEventHandler implements EventHandler {

    private final TransfersApplicationService transfersApplicationService;


    @Inject
    public TransferEventHandler(DomainEventPublisher domainEventPublisher, TransfersApplicationService transfersApplicationService) {
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

    private Void runAsync(Runnable runnable) {
        runnable.run();
        return null;
    }
}
