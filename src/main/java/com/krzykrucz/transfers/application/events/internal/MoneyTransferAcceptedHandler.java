package com.krzykrucz.transfers.application.events.internal;

import com.krzykrucz.transfers.application.AccountApplicationService;
import com.krzykrucz.transfers.application.api.command.AcceptTransferCommand;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferAccepted;
import com.krzykrucz.transfers.domain.common.DomainEventHandler;

public class MoneyTransferAcceptedHandler implements DomainEventHandler<MoneyTransferAccepted> {

    private final AccountApplicationService accountApplicationService;

    public MoneyTransferAcceptedHandler(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @Override
    public void handle(MoneyTransferAccepted event) {
        accountApplicationService.acceptTransfer(new AcceptTransferCommand(event.getTransferReferenceNumber()));
    }

}
