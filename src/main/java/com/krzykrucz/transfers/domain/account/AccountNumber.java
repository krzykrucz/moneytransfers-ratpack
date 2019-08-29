package com.krzykrucz.transfers.domain.account;

import lombok.EqualsAndHashCode;

import java.util.regex.Pattern;

import static com.krzykrucz.transfers.domain.common.DomainException.checkDomainState;

@EqualsAndHashCode
public class AccountNumber {

    private static final Pattern VALID_PATTERN = Pattern.compile("[0-9]{2,30}");

    private final String number;

    public AccountNumber(String number) {
        checkDomainState(number != null && VALID_PATTERN.matcher(number).matches(),
                "Wrong account number");
        this.number = number;
    }

    @Override
    public String toString() {
        return number;
    }
}
