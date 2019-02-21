package com.krzykrucz.transfers.adapters.retry;

import com.google.inject.AbstractModule;
import com.krzykrucz.transfers.application.ResilientAccountApplicationService;
import io.github.resilience4j.ratpack.Resilience4jModule;

public class ResilienceConfig extends AbstractModule {

    @Override
    // TODO fork r4j
    protected void configure() {
        Resilience4jModule module = new Resilience4jModule();
        module.configure(c -> c
                .retry("retryExceptions", retryConfig -> retryConfig
                        .maxAttempts(4)
                        .waitDurationInMillis(100)
                ));
        install(module);

        bind(ResilientAccountApplicationService.class).to(RetryingAccountApplicationService.class);
    }

}
