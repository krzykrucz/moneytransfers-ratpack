package com.krzykrucz.transfers.application.api.command;

import com.krzykrucz.transfers.domain.account.TransferReferenceNumber;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class RejectTransferCommand {

    private String transferReferenceNumber;

    public RejectTransferCommand(TransferReferenceNumber referenceNumber) {
        this(referenceNumber.toString());
    }

    public TransferReferenceNumber getTransferReferenceNumber() {
        return TransferReferenceNumber.fromExisting(transferReferenceNumber);
    }
}
