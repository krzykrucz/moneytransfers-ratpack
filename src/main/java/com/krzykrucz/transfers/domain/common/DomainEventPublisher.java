package com.krzykrucz.transfers.domain.common;

public interface DomainEventPublisher {

    void publish(DomainEvent event);

}
