package com.krzykrucz.transfers.domain.common;

import com.krzykrucz.transfers.domain.account.AccountIdentifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@EqualsAndHashCode
public abstract class DomainEvent {

    @Getter
    private final AccountIdentifier accountId;

    @Getter
    private final Instant creationTime;

    protected DomainEvent(AccountIdentifier accountIdentifier) {
        this.accountId = accountIdentifier;
        this.creationTime = Instant.now();
    }
}
