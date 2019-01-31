package com.krzykrucz.transfers.domain.account.event;

import com.krzykrucz.transfers.domain.DomainEvent;
import com.krzykrucz.transfers.domain.account.AccountIdentifier;
import com.krzykrucz.transfers.domain.account.MoneyTransfer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.joda.money.Money;

@Getter
@EqualsAndHashCode(callSuper = true)
public class MoneyTransferCommissioned extends DomainEvent {

    private final Money blockedMoney;

    private final MoneyTransfer moneyTransfer;

    public MoneyTransferCommissioned(AccountIdentifier accountIdentifier, Money blockedMoney, MoneyTransfer moneyTransfer) {
        super(accountIdentifier);
        this.blockedMoney = blockedMoney;
        this.moneyTransfer = moneyTransfer;
    }
}
