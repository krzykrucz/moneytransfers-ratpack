package com.krzykrucz.transfers.application.api.command;

import com.krzykrucz.transfers.domain.account.AccountNumber;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.Money;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class PerformMoneyTransferCommand {

    private String from;

    private String to;

    private Money value;

    public AccountNumber getFrom() {
        return new AccountNumber(from);
    }

    public AccountNumber getTo() {
        return new AccountNumber(to);
    }

    public Money getValue() {
        return value;
    }
}
