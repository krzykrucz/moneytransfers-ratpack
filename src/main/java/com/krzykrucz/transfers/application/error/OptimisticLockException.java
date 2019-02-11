package com.krzykrucz.transfers.application.error;

public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException() {
        super("Conflict modifying multiple accounts at the same time");
    }

}
