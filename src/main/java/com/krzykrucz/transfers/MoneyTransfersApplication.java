package com.krzykrucz.transfers;

import com.krzykrucz.transfers.application.api.MoneyTransfersAPI;
import com.krzykrucz.transfers.infrastructure.guice.GuiceConfig;
import com.krzykrucz.transfers.infrastructure.retry.ResilienceConfig;
import ratpack.guice.Guice;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public class MoneyTransfersApplication {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(s -> s
                .serverConfig(c -> c.baseDir(BaseDir.find()))
                .registry(Guice.registry(b -> b
                        .module(GuiceConfig.class)
                        .module(ResilienceConfig.class)))
                .handlers(new MoneyTransfersAPI())
        );
    }

}