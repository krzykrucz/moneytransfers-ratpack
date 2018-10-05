package com.krzykrucz.transfers;

import com.google.inject.AbstractModule;
import com.krzykrucz.transfers.application.api.CreateAccountHandler;
import com.krzykrucz.transfers.application.TransferEventHandler;
import com.krzykrucz.transfers.application.TransfersApplicationService;
import com.krzykrucz.transfers.application.api.DepositMoneyHandler;
import com.krzykrucz.transfers.application.api.GetAccountHandler;
import com.krzykrucz.transfers.application.api.TransferCommandHandler;
import com.krzykrucz.transfers.domain.CurrencyExchanger;
import com.krzykrucz.transfers.domain.DomainEventPublisher;
import com.krzykrucz.transfers.domain.EventHandler;
import com.krzykrucz.transfers.domain.account.AccountRepository;
import com.krzykrucz.transfers.infrastructure.ExternalCurrencyExchanger;
import com.krzykrucz.transfers.infrastructure.InMemoryAccountRepository;
import com.google.inject.multibindings.Multibinder;
import lombok.extern.slf4j.Slf4j;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.handling.HandlerDecorator;


public class Config extends AbstractModule {

    @Override
    protected void configure() {
        bind(DomainEventPublisher.class);
        bind(EventHandler.class).to(TransferEventHandler.class).asEagerSingleton();
        bind(ExternalCurrencyExchanger.class);
        bind(AccountRepository.class).to(InMemoryAccountRepository.class);
        bind(TransfersApplicationService.class);
        bind(TransferCommandHandler.class);
        bind(CreateAccountHandler.class);
        bind(GetAccountHandler.class);
        bind(DepositMoneyHandler.class);
        Multibinder.newSetBinder(binder(), HandlerDecorator.class)
                .addBinding()
                .toInstance(HandlerDecorator.prepend(new LoggingHandler()));
    }

    @Slf4j
    private static class LoggingHandler implements Handler {

        @Override
        public void handle(Context context) {
            log.info("Received: " + context.getRequest().getUri());
            context.next();
        }
    }
}