package com.krzykrucz.transfers.domain.common;

public interface DomainEventHandler<E extends DomainEvent> {

    void handle(E domainEvent);

}
