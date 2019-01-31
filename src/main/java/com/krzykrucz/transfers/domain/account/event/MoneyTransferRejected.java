package com.krzykrucz.transfers.domain.account.event;

import com.krzykrucz.transfers.domain.account.AccountIdentifier;
import com.krzykrucz.transfers.domain.account.TransferReferenceNumber;
import com.krzykrucz.transfers.domain.common.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class MoneyTransferRejected extends DomainEvent {

    private final TransferReferenceNumber referenceNumber;

    public MoneyTransferRejected(AccountIdentifier accountIdentifier, TransferReferenceNumber referenceNumber) {
        super(accountIdentifier);
        this.referenceNumber = referenceNumber;
    }
}
