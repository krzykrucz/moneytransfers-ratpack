package com.krzykrucz.transfers.domain.util

import com.krzykrucz.transfers.domain.account.event.MoneyTransferAccepted
import com.krzykrucz.transfers.domain.account.event.MoneyTransferRejected
import com.krzykrucz.transfers.domain.common.DomainEvent
import com.krzykrucz.transfers.infrastructure.events.DomainEventPublisherImpl
import spock.util.concurrent.BlockingVariable;

class EventPublisherSpy extends DomainEventPublisherImpl {
    def transferAcceptedEventReceived = new BlockingVariable<Boolean>(5)
    def transferRejectedEventReceived = new BlockingVariable<Boolean>(5)

    boolean checkTransferAcceptedEventReceived() {
        transferAcceptedEventReceived.get()
    }

    boolean checkTransferRejectedEventReceived() {
        transferRejectedEventReceived.get()
    }

    def reset() {
        transferRejectedEventReceived.set(false)
        transferAcceptedEventReceived.set(false)
    }

    @Override
    void publish(DomainEvent event) {
        super.publish(event)
        if (event instanceof MoneyTransferAccepted) {
            transferAcceptedEventReceived.set(true)
        }
        if (event instanceof MoneyTransferRejected) {
            transferRejectedEventReceived.set(true)
        }
    }
}