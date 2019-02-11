package com.krzykrucz.transfers.infrastructure.retry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.krzykrucz.transfers.application.TransfersApplicationService;
import com.krzykrucz.transfers.application.api.command.*;
import com.krzykrucz.transfers.domain.account.TransferReferenceNumber;
import com.krzykrucz.transfers.domain.common.DomainException;
import com.krzykrucz.transfers.infrastructure.guice.SecondarySingleton;
import io.github.resilience4j.ratpack.recovery.RecoveryFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;

import static io.github.resilience4j.retry.Retry.decorateCheckedRunnable;

@Singleton
public class RetryingTransferApplicationService implements TransfersApplicationService {

    private final TransfersApplicationService service;

    private final Retry operationRetry;

    @Inject
    public RetryingTransferApplicationService(@SecondarySingleton TransfersApplicationService service) {

        this.service = service;
        this.operationRetry = Retry.of("operationRetry", RetryConfig.custom()
                .maxAttempts(4)
                .intervalFunction(integer -> (long) integer * 2)
                .ignoreExceptions(DomainException.class)
                .build());
    }

    @Override
    public void transfer(PerformMoneyTransferCommand moneyTransferCommand) {
        withRetries(() -> service.transfer(moneyTransferCommand));
    }

    @Override
    public void openAccount(OpenAccountCommand openAccountCommand) {
        withRetries(() -> service.openAccount(openAccountCommand));
    }

    @Override
    public void depositMoney(DepositMoneyCommand depositMoneyCommand) {
        withRetries(() -> service.depositMoney(depositMoneyCommand));
    }

    @Override
    public void acceptTransfer(AcceptTransferCommand acceptTransferCommand) {
        withRetries(() -> service.acceptTransfer(acceptTransferCommand));
    }

    @Override
    public void rejectTransfer(RejectTransferCommand rejectTransferCommand) {
        withRetries(() -> service.rejectTransfer(rejectTransferCommand));
    }

    @Override
    public void receiveTransfer(ReceiveTransferCommand receiveTransferCommand) {
        final TransferFailedRecovery operationRecovery =
                new TransferFailedRecovery(service, receiveTransferCommand.getTransferReferenceNumber());
        withRetries(() -> service.receiveTransfer(receiveTransferCommand), operationRecovery);
    }

    private void withRetries(CheckedRunnable operation) {
        final CheckedRunnable retryableOperation = decorateCheckedRunnable(operationRetry, operation);
        final Try<Void> tryOperation = Try.run(retryableOperation);

        tryOperation.get();
    }

    private void withRetries(CheckedRunnable operation, RecoveryFunction<Void> recovery) {
        final CheckedRunnable retryableOperation = decorateCheckedRunnable(operationRetry, operation);
//        final Try<Void> tryOperation = Try.run(retryableOperation).recover(recovery);
//
//        tryOperation.get();
    }

    private class TransferFailedRecovery implements RecoveryFunction<Void> {

        private final TransfersApplicationService service;
        private final TransferReferenceNumber failedTransfer;

        private TransferFailedRecovery(TransfersApplicationService service, TransferReferenceNumber failedTransfer) {
            this.service = service;
            this.failedTransfer = failedTransfer;
        }

//        @Override
//        public void run() {
//            final RejectTransferCommand rejectTransferCommand = new RejectTransferCommand(failedTransfer);
//            service.rejectTransfer(rejectTransferCommand);
//        }

        @Override
        public Void apply(Throwable throwable) {
            if (throwable instanceof DomainException) {
                return null;
            }
            final RejectTransferCommand rejectTransferCommand = new RejectTransferCommand(failedTransfer);
            service.rejectTransfer(rejectTransferCommand);

            return null;
        }
    }
}
