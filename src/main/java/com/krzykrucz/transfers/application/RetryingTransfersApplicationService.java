package com.krzykrucz.transfers.application;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.krzykrucz.transfers.application.api.command.*;
import io.github.resilience4j.ratpack.retry.Retry;

@Singleton
public class RetryingTransfersApplicationService implements TransfersApplicationService {

    private final DomainAPI domain;

    @Inject
    public RetryingTransfersApplicationService(DomainAPI domain) {
        this.domain = domain;
    }

    @Override
    @Retry(name = "retryExceptions")
    public void transfer(PerformMoneyTransferCommand moneyTransferCommand) {
        domain.transfer(moneyTransferCommand);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void openAccount(OpenAccountCommand openAccountCommand) {
        domain.openAccount(openAccountCommand);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void depositMoney(DepositMoneyCommand depositMoneyCommand) {
        domain.depositMoney(depositMoneyCommand);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void acceptTransfer(AcceptTransferCommand acceptTransferCommand) {
        domain.acceptTransfer(acceptTransferCommand);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void rejectTransfer(RejectTransferCommand rejectTransferCommand) {
        domain.rejectTransfer(rejectTransferCommand);
    }

    @Override
    @Retry(name = "retryExceptions")
    public void receiveTransfer(ReceiveTransferCommand receiveTransferCommand) {
        domain.receiveTransfer(receiveTransferCommand);
    }
}
