package com.krzykrucz.transfers.domain.account;

import com.krzykrucz.transfers.domain.common.DomainException;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.joda.money.Money;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
class PendingTransfers {

    private Map<TransferReferenceNumber, Money> moneyBlockedOnTransfers;

    static PendingTransfers empty() {
        return new PendingTransfers(HashMap.empty());
    }

    PendingTransfers addTransfer(MoneyTransfer transfer) {
        return copy(moneyBlockedOnTransfers.put(transfer.getReferenceNumber(), transfer.getValue()));
    }

    PendingTransfers removeTransfer(TransferReferenceNumber referenceNumber) {
        return copy(moneyBlockedOnTransfers.remove(referenceNumber));
    }

    Money getMoneyBlockedOn(TransferReferenceNumber referenceNumber) {
        final Option<Money> moneyBlocked = this.moneyBlockedOnTransfers.get(referenceNumber);
        return moneyBlocked
                .getOrElseThrow(() -> new DomainException("No pending transfer with that reference"));
    }

    Set<TransferReferenceNumber> getReferenceNumbers() {
        return moneyBlockedOnTransfers.keySet();
    }

    private PendingTransfers copy(Map<TransferReferenceNumber, Money> moneyBlockedOnTransfers) {
        return new PendingTransfers(moneyBlockedOnTransfers);
    }
}
