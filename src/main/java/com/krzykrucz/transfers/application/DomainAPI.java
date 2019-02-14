package com.krzykrucz.transfers.application;

import com.krzykrucz.transfers.application.api.command.*;

public interface DomainAPI {
    void transfer(PerformMoneyTransferCommand moneyTransferCommand);

    void openAccount(OpenAccountCommand openAccountCommand);

    void depositMoney(DepositMoneyCommand depositMoneyCommand);

    void acceptTransfer(AcceptTransferCommand acceptTransferCommand);

    void rejectTransfer(RejectTransferCommand rejectTransferCommand);

    void receiveTransfer(ReceiveTransferCommand receiveTransferCommand);
}
