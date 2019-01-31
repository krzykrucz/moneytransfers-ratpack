package com.krzykrucz.transfers.infrastructure.events;

import com.google.common.collect.Lists;
import com.krzykrucz.transfers.application.api.handlers.internal.DomainEventHandler;
import com.krzykrucz.transfers.domain.DomainEvent;
import com.krzykrucz.transfers.domain.DomainEventPublisher;

import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class DomainEventPublisherImpl implements DomainEventPublisher {

    private final Collection<DomainEventHandler> handlers = Lists.newCopyOnWriteArrayList();

    @Override
    public void publish(DomainEvent event) {
        handlers.forEach(eventHandler -> eventHandler.handle(event));
    }

    @Override
    public void subscribe(DomainEventHandler handler) {
        handlers.add(handler);
    }

}
