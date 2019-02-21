package com.krzykrucz.transfers.adapters.events;

import com.google.common.eventbus.EventBus;
import com.krzykrucz.transfers.domain.common.DomainEvent;
import com.krzykrucz.transfers.domain.common.DomainEventHandler;
import com.krzykrucz.transfers.domain.common.DomainEventPublisher;
import lombok.NoArgsConstructor;

import javax.inject.Singleton;

@Singleton
@NoArgsConstructor
public class GuavaDomainEventPublisher implements DomainEventPublisher {

    private final EventBus syncEventBus = new EventBus();

    @Override
    public <E extends DomainEvent> void subcribe(DomainEventHandler<E> domainEventHandler) {
        syncEventBus.register(new GuavaEventBusSubscriber<>(domainEventHandler));
    }

    @Override
    public void publish(DomainEvent event) {
        syncEventBus.post(event);
    }

}
