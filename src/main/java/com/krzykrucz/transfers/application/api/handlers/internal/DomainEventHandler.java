package com.krzykrucz.transfers.application.api.handlers.internal;

import com.krzykrucz.transfers.domain.common.DomainEvent;

public interface DomainEventHandler<E extends DomainEvent> {

    void handle(E domainEvent);

}
