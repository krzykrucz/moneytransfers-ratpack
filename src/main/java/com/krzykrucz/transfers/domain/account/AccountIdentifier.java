package com.krzykrucz.transfers.domain.account;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountIdentifier {

    private final UUID uuid;

    public static AccountIdentifier generate() {
        return new AccountIdentifier(UUID.randomUUID());
    }

    public static AccountIdentifier fromExisting(String id) {
        return new AccountIdentifier(UUID.fromString(id));
    }
}
