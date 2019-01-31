package com.krzykrucz.transfers.domain.common;

public class DomainException extends RuntimeException {

    private DomainException(String message) {
        super(message);
    }

    public static void checkDomainState(boolean predicate, String message) {
        if (predicate) {
            return;
        }
        throw new DomainException(message);
    }

}
