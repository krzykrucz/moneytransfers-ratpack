package com.krzykrucz.transfers;

import com.krzykrucz.transfers.application.api.MoneyTransfersAPI;
import com.krzykrucz.transfers.infrastructure.guice.GuiceConfig;
import com.krzykrucz.transfers.infrastructure.retry.ResilienceConfig;
import ratpack.guice.Guice;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public class MoneyTransfersApplication {

    // TODO add resilience4j retries, specifically to internal event handlers (test that)
    public static void main(String[] args) throws Exception {
        RatpackServer.start(s -> s
                .serverConfig(c -> c.baseDir(BaseDir.find()))
                .registry(Guice.registry(b -> b
                        .module(GuiceConfig.class).module(new ResilienceConfig())))
                .handlers(new MoneyTransfersAPI())
        );
    }

}