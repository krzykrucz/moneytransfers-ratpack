package com.krzykrucz.transfers.application.api.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.krzykrucz.transfers.application.jackson.MoneyDeserializer;
import com.krzykrucz.transfers.domain.account.AccountNumber;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.Money;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Setter
public class DepositMoneyCommand {

    @Getter
    @JsonDeserialize(using = MoneyDeserializer.class)
    private Money value;

    private String accountNumber;

    public AccountNumber getAccountNumber() {
        return new AccountNumber(accountNumber);
    }

}
