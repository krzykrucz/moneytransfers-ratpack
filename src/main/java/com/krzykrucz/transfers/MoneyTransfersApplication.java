package com.krzykrucz.transfers;

import com.krzykrucz.transfers.application.api.MoneyTransfersAPI;
import ratpack.guice.Guice;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public class MoneyTransfersApplication {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(s -> s
                .serverConfig(c -> c.baseDir(BaseDir.find()))
                .registry(Guice.registry(b -> b.module(Config.class)))
                .handlers(new MoneyTransfersAPI())
        );
    }

}