package com.krzykrucz.transfers.domain;

import com.google.common.collect.Lists;

import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class DomainEventPublisher {

    private final Collection<EventHandler> handlers = Lists.newArrayList();

    public void publish(DomainEvent event) {
        handlers.forEach(eventHandler -> eventHandler.handle(event));
    }

    public void subscribe(EventHandler handler) {
        handlers.add(handler);
    }

}
