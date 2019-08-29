package com.krzykrucz.transfers.appconfig;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.krzykrucz.transfers.adapters.events.GuavaDomainEventPublisher;
import com.krzykrucz.transfers.adapters.exchanger.IdentityCurrencyExchanger;
import com.krzykrucz.transfers.adapters.persistence.InMemoryAccountRepository;
import com.krzykrucz.transfers.adapters.rest.handlers.CreateAccountHandler;
import com.krzykrucz.transfers.adapters.rest.handlers.DepositMoneyHandler;
import com.krzykrucz.transfers.adapters.rest.handlers.GetAccountHandler;
import com.krzykrucz.transfers.adapters.rest.handlers.TransferCommandHandler;
import com.krzykrucz.transfers.adapters.retry.RetryingAccountApplicationService;
import com.krzykrucz.transfers.application.AccountApplicationService;
import com.krzykrucz.transfers.application.AccountApplicationServiceImpl;
import com.krzykrucz.transfers.application.ResilientAccountApplicationService;
import com.krzykrucz.transfers.domain.CurrencyExchanger;
import com.krzykrucz.transfers.domain.account.AccountRepository;
import com.krzykrucz.transfers.domain.common.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.handling.HandlerDecorator;

public class GuiceConfig extends AbstractModule {

    @Override
    protected void configure() {
        bind(DomainEventPublisher.class).to(GuavaDomainEventPublisher.class).asEagerSingleton();

        bind(CurrencyExchanger.class).to(IdentityCurrencyExchanger.class);
        bind(AccountRepository.class).to(InMemoryAccountRepository.class);
        bind(AccountApplicationService.class).to(AccountApplicationServiceImpl.class);
        bind(ResilientAccountApplicationService.class).to(RetryingAccountApplicationService.class);
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