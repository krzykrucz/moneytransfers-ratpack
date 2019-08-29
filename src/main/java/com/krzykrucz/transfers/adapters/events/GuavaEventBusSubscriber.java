package com.krzykrucz.transfers.adapters.events;

import com.google.common.eventbus.Subscribe;
import com.krzykrucz.transfers.domain.common.DomainEvent;
import com.krzykrucz.transfers.domain.common.DomainEventHandler;

class GuavaEventBusSubscriber<E extends DomainEvent> implements DomainEventHandler<E> {

    private final DomainEventHandler<E> delegate;

    GuavaEventBusSubscriber(DomainEventHandler<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    @Subscribe
//    @AllowConcurrentEvents
    public void handle(E domainEvent) {
        delegate.handle(domainEvent);
    }
}
