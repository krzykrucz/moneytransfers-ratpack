package com.krzykrucz.transfers.adapters.persistence;

public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException() {
        super("Conflict modifying multiple accounts at the same time");
    }

}
