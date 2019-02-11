package com.krzykrucz.transfers.application.api.command;

import com.krzykrucz.transfers.domain.account.MoneyTransfer;
import com.krzykrucz.transfers.domain.account.TransferReferenceNumber;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.Money;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PRIVATE)
public class ReceiveTransferCommand extends PerformMoneyTransferCommand {

    private String transferReferenceNumber;

    public ReceiveTransferCommand(String from, String to, Money value, String transferRefNumber) {
        super(from, to, value);
        this.transferReferenceNumber = transferRefNumber;
    }

    public ReceiveTransferCommand(MoneyTransfer transfer) {
        this(
                transfer.getFrom().toString(),
                transfer.getTo().toString(),
                transfer.getValue(),
                transfer.getReferenceNumber().toString()
        );
    }

    public TransferReferenceNumber getTransferReferenceNumber() {
        return TransferReferenceNumber.fromExisting(transferReferenceNumber);
    }
}
