package com.krzykrucz.transfers.domain.account;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
@EqualsAndHashCode
public class TransferReferenceNumber {

    private final UUID uuid;

    static TransferReferenceNumber generate() {
        return new TransferReferenceNumber(UUID.randomUUID());
    }
}
