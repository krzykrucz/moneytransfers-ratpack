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

    public static TransferReferenceNumber fromExisting(String ref) {
        return new TransferReferenceNumber(UUID.fromString(ref));
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
