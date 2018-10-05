package com.krzykrucz.transfers.domain;

@FunctionalInterface
public interface EventHandler {

    void handle(DomainEvent domainEvent);

}
