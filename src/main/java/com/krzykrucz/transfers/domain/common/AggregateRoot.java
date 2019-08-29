package com.krzykrucz.transfers.domain.common;

import io.vavr.collection.List;
import lombok.Getter;

public abstract class AggregateRoot {

    @Getter
    private List<DomainEvent> domainEvents = List.empty();

    @Getter
    private long version;

    protected AggregateRoot(long version) {
        this.version = version;
    }

    @SuppressWarnings("unused")
    public abstract AggregateId getId();

    protected void publishEvent(DomainEvent event) {
        domainEvents = domainEvents.append(event);
    }

    public boolean hasSameVersionAs(long anotherVersion) {
        return version == anotherVersion;
    }

    public void finishModification() {
        domainEvents = List.empty();
        version++;
    }

}
