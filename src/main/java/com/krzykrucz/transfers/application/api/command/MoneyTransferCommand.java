package com.krzykrucz.transfers.application.api.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.krzykrucz.transfers.application.jackson.MoneyDeserializer;
import com.krzykrucz.transfers.domain.account.AccountNumber;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.Money;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class MoneyTransferCommand {

    private String from;

    private String to;

    @JsonDeserialize(using = MoneyDeserializer.class)
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
