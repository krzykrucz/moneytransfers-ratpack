package com.krzykrucz.transfers.domain.common;

import io.vavr.collection.List;

public abstract class Aggregate {

    private List<DomainEvent> domainEvents = List.empty();

    public abstract AggregateId getId();

    protected void publishEvent(DomainEvent event) {
        domainEvents = domainEvents.append(event);
    }

    public List<DomainEvent> getEventsAndFlush() {
        final List<DomainEvent> eventsCopy = domainEvents;
        domainEvents = List.empty();
        return eventsCopy;
    }

}
