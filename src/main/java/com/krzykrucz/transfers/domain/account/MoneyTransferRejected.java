package com.krzykrucz.transfers.domain.account;

import com.krzykrucz.transfers.domain.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class MoneyTransferRejected extends DomainEvent {

    private final TransferReferenceNumber referenceNumber;

}
