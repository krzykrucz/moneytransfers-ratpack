package com.krzykrucz.transfers.application.api.command;

import com.krzykrucz.transfers.domain.account.AccountNumber;
import lombok.*;
import org.joda.money.Money;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PRIVATE)
public class DepositMoneyCommand {

    @Getter
    private Money value;

    private String accountNumber;

    public AccountNumber getAccountNumber() {
        return new AccountNumber(accountNumber);
    }

}
