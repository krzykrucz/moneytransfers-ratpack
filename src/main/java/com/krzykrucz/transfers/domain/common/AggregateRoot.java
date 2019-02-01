package com.krzykrucz.transfers.domain.common;

import io.vavr.collection.List;
import lombok.Getter;

public abstract class AggregateRoot {

    private List<DomainEvent> domainEvents = List.empty();

    @Getter
    private long version;

    protected AggregateRoot(long version) {
        this.version = version;
    }

    public abstract AggregateId getId();

    protected void publishEvent(DomainEvent event) {
        domainEvents = domainEvents.append(event);
    }

    public boolean hasSameVersionAs(long anotherVersion) {
        return version == anotherVersion;
    }

    public List<DomainEvent> finishModification() {
        final List<DomainEvent> eventsCopy = domainEvents;
        domainEvents = List.empty();
        version++;
        return eventsCopy;
    }

}
