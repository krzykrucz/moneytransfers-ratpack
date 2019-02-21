package com.krzykrucz.transfers.domain.util

import com.krzykrucz.transfers.domain.account.event.MoneyTransferAccepted
import com.krzykrucz.transfers.domain.account.event.MoneyTransferCommissioned
import com.krzykrucz.transfers.domain.account.event.MoneyTransferRejected
import com.krzykrucz.transfers.domain.common.DomainEvent
import com.krzykrucz.transfers.domain.common.DomainEventHandler
import com.krzykrucz.transfers.domain.common.DomainEventPublisher

class EventPublisherMock implements DomainEventPublisher {

    def publishedEvents = []

    def lastCommissionedTransfer

    def handlers = []

    @Override
    def <E extends DomainEvent> void subcribe(DomainEventHandler<E> domainEventHandler) {
        handlers << domainEventHandler
    }

    @Override
    void publish(DomainEvent event) {
        publishedEvents << event

        for (def handler : handlers) {
            try {
                handler.handle(event)
            } catch (Exception ex) {
            }
        }
    }

    def reset() {
        publishedEvents.clear()
    }

    boolean checkTransferAcceptedEventReceived() {
        removeIfEventIsInstanceOf(MoneyTransferAccepted)
    }

    boolean checkTransferRejectedEventReceived() {
        removeIfEventIsInstanceOf(MoneyTransferRejected)
    }

    boolean checkTransferCommissionedEventReceived() {
        MoneyTransferCommissioned event = publishedEvents.find { it.getClass() == MoneyTransferCommissioned }
        lastCommissionedTransfer = event.moneyTransfer
        return event != null
    }

    private boolean removeIfEventIsInstanceOf(Class<? extends DomainEvent> aClass) {
        publishedEvents.removeIf { it.getClass() == aClass }
    }
}