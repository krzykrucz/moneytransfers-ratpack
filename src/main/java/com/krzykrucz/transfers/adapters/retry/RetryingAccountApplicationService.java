package com.krzykrucz.transfers.adapters.retry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.krzykrucz.transfers.application.AccountApplicationService;
import com.krzykrucz.transfers.application.ResilientAccountApplicationService;
import com.krzykrucz.transfers.application.api.command.*;
import io.github.resilience4j.ratpack.retry.Retry;

@Singleton
public class RetryingAccountApplicationService implements ResilientAccountApplicationService {

    private final AccountApplicationService service;

    @Inject
    public RetryingAccountApplicationService(AccountApplicationService service) {
        this.service = service;
    }

    @Override
    @Retry(name = "retryExceptions")
    public void transfer(PerformMoneyTransferCommand moneyTransferCommand) {
        service.transfer(moneyTransferCommand);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void openAccount(OpenAccountCommand openAccountCommand) {
        service.openAccount(openAccountCommand);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void depositMoney(DepositMoneyCommand depositMoneyCommand) {
        service.depositMoney(depositMoneyCommand);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void acceptTransfer(AcceptTransferCommand acceptTransferCommand) {
        service.acceptTransfer(acceptTransferCommand);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void rejectTransfer(RejectTransferCommand rejectTransferCommand) {
        service.rejectTransfer(rejectTransferCommand);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void receiveTransfer(ReceiveTransferCommand receiveTransferCommand) {
        service.receiveTransfer(receiveTransferCommand);
    }
}
