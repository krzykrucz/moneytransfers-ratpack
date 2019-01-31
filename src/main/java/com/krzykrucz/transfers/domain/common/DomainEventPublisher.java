package com.krzykrucz.transfers.domain.common;

import com.krzykrucz.transfers.application.api.handlers.internal.DomainEventHandler;

public interface DomainEventPublisher {
    void publish(DomainEvent event);

    void subscribe(DomainEventHandler handler);
}
