package com.krzykrucz.transfers.domain.common;

public interface DomainEventPublisher {

    <E extends DomainEvent> void subcribe(DomainEventHandler<E> domainEventHandler);

    void publish(DomainEvent event);

}
