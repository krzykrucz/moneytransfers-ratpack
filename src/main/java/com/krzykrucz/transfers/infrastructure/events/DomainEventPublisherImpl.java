package com.krzykrucz.transfers.infrastructure.events;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.krzykrucz.transfers.application.api.handlers.internal.DomainEventHandler;
import com.krzykrucz.transfers.domain.common.DomainEvent;
import com.krzykrucz.transfers.domain.common.DomainEventPublisher;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;

@Singleton
@NoArgsConstructor
public class DomainEventPublisherImpl implements DomainEventPublisher {

    private final EventBus asyncEventBus = new AsyncEventBus(Executors.newCachedThreadPool());

    @Inject
    public DomainEventPublisherImpl(Set<DomainEventHandler> domainEventHandlers) {
        domainEventHandlers.forEach(asyncEventBus::register);
    }

    public void subcribe(Collection<DomainEventHandler> domainEventHandlers) {
        domainEventHandlers.forEach(asyncEventBus::register);
    }

    @Override
    public void publish(DomainEvent event) {
        asyncEventBus.post(event);
    }

}
