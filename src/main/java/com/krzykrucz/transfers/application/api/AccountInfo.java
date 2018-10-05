package com.krzykrucz.transfers.application.api;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.krzykrucz.transfers.application.jackson.MoneySerializer;
import com.krzykrucz.transfers.domain.account.Account;
import lombok.Getter;
import org.joda.money.Money;

@Getter
class AccountInfo {

    private final String accountNumber;

    @JsonSerialize(using = MoneySerializer.class)
    private final Money balance;

    AccountInfo(Account account) {
        this.accountNumber = account.getNumber().toString();
        this.balance = account.getBalance();
    }


}
