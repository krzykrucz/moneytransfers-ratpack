package com.krzykrucz.transfers.application.events.internal;

import com.krzykrucz.transfers.application.AccountApplicationService;
import com.krzykrucz.transfers.application.api.command.ReceiveTransferCommand;
import com.krzykrucz.transfers.domain.account.event.MoneyTransferCommissioned;
import com.krzykrucz.transfers.domain.common.DomainEventHandler;

public class MoneyTransferCommissionedHandler implements DomainEventHandler<MoneyTransferCommissioned> {

    private final AccountApplicationService accountApplicationService;

    public MoneyTransferCommissionedHandler(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @Override
    public void handle(MoneyTransferCommissioned event) {
        accountApplicationService.receiveTransfer(new ReceiveTransferCommand(event.getMoneyTransfer()));
    }

}
