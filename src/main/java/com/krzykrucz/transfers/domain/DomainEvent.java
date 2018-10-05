package com.krzykrucz.transfers.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@EqualsAndHashCode
public abstract class DomainEvent {

    @Getter
    private final Instant creationTime;

    protected DomainEvent() {
        this.creationTime = Instant.now();
    }
}
