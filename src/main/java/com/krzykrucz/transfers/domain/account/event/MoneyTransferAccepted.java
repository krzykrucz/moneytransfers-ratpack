package com.krzykrucz.transfers.domain.account.event;

import com.krzykrucz.transfers.domain.DomainEvent;
import com.krzykrucz.transfers.domain.account.AccountIdentifier;
import com.krzykrucz.transfers.domain.account.TransferReferenceNumber;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class MoneyTransferAccepted extends DomainEvent {

    private final TransferReferenceNumber transferReferenceNumber;

    public MoneyTransferAccepted(AccountIdentifier accountIdentifier, TransferReferenceNumber transferReferenceNumber) {
        super(accountIdentifier);
        this.transferReferenceNumber = transferReferenceNumber;
    }

}
