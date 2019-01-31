package com.krzykrucz.transfers.infrastructure.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.krzykrucz.transfers.application.TransfersApplicationService;
import com.krzykrucz.transfers.application.api.handlers.CreateAccountHandler;
import com.krzykrucz.transfers.application.api.handlers.DepositMoneyHandler;
import com.krzykrucz.transfers.application.api.handlers.GetAccountHandler;
import com.krzykrucz.transfers.application.api.handlers.TransferCommandHandler;
import com.krzykrucz.transfers.application.api.handlers.internal.DomainEventHandler;
import com.krzykrucz.transfers.application.api.handlers.internal.TransferInternalEventHandler;
import com.krzykrucz.transfers.domain.CurrencyExchanger;
import com.krzykrucz.transfers.domain.DomainEventPublisher;
import com.krzykrucz.transfers.domain.account.AccountRepository;
import com.krzykrucz.transfers.infrastructure.events.DomainEventPublisherImpl;
import com.krzykrucz.transfers.infrastructure.exchanger.IdentityCurrencyExchanger;
import com.krzykrucz.transfers.infrastructure.persistence.InMemoryAccountRepository;
import lombok.extern.slf4j.Slf4j;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.handling.HandlerDecorator;


public class Config extends AbstractModule {

    @Override
    protected void configure() {
        bind(DomainEventPublisher.class).to(DomainEventPublisherImpl.class).asEagerSingleton();
        bind(DomainEventHandler.class).to(TransferInternalEventHandler.class).asEagerSingleton();
        bind(CurrencyExchanger.class).to(IdentityCurrencyExchanger.class);
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