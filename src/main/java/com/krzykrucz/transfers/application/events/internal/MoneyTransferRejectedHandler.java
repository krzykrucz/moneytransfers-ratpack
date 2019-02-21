package com.krzykrucz.transfers.application.events.internal;

import com.krzykrucz.transfers.application.AccountApplicationService;
import com.krzykrucz.transfers.application.api.command.RejectTransferCommand;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferRejected;
import com.krzykrucz.transfers.domain.common.DomainEventHandler;

public class MoneyTransferRejectedHandler implements DomainEventHandler<MoneyTransferRejected> {

    private final AccountApplicationService accountApplicationService;

    public MoneyTransferRejectedHandler(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @Override
    public void handle(MoneyTransferRejected event) {
        accountApplicationService.rejectTransfer(new RejectTransferCommand(event.getReferenceNumber()));
    }

}
