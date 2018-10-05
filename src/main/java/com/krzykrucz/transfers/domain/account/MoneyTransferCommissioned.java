package com.krzykrucz.transfers.domain.account;

import com.krzykrucz.transfers.domain.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.money.Money;

@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class MoneyTransferCommissioned extends DomainEvent {

    private final Money blockedMoney;

    private final MoneyTransfer moneyTransfer;

}
