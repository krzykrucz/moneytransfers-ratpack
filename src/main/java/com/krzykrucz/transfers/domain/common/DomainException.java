package com.krzykrucz.transfers.domain.common;

public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public static void checkDomainState(boolean predicate, String message) {
        if (predicate) {
            return;
        }
        throw new DomainException(message);
    }

    public static <T> T checkNotNull(T reference, String name) {
        if (reference == null) {
            throw new DomainException("Missing property: " + name);
        }
        return reference;
    }

}
