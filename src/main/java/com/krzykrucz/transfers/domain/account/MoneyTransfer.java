package com.krzykrucz.transfers.domain.account;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.joda.money.Money;

@Getter
@EqualsAndHashCode(of = "referenceNumber")
@AllArgsConstructor
public class MoneyTransfer {
    private final TransferReferenceNumber referenceNumber;
    private final Money value;
    private final AccountNumber from;
    private final AccountNumber to;

    static MoneyTransfer generateMoneyTransfer(Money value, AccountNumber from, AccountNumber to) {
        return new MoneyTransfer(TransferReferenceNumber.generate(), value, from, to);
    }
}
