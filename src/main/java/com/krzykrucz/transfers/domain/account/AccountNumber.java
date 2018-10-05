package com.krzykrucz.transfers.domain.account;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

@EqualsAndHashCode
public class AccountNumber {

    private static final Pattern VALID_PATTERN = Pattern.compile("[0-9]{2,30}");

    private final String number;

    public AccountNumber(String number) {
        checkArgument(VALID_PATTERN.matcher(number).matches());
        this.number = number;
    }

    @Override
    public String toString() {
        return number;
    }
}
