package com.krzykrucz.transfers.application.api.handlers.internal;

import com.krzykrucz.transfers.domain.DomainEvent;

@FunctionalInterface
public interface DomainEventHandler {

    void handle(DomainEvent domainEvent);

}
