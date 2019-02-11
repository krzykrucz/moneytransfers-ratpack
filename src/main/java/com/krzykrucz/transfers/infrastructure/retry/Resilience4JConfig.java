package com.krzykrucz.transfers.infrastructure.retry;

import com.google.inject.AbstractModule;
import io.github.resilience4j.ratpack.Resilience4jModule;

public class Resilience4JConfig extends AbstractModule {

    @Override
    protected void configure() {
        Resilience4jModule module = new Resilience4jModule();
        module.configure(c -> c
                .retry("retryOptimisticLocks", retryConfig -> retryConfig
                        .maxAttempts(3)
                        .waitDurationInMillis(100)
                ));
        install(module);
    }

}
