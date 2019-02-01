package com.krzykrucz.transfers.application.api.command;

import com.krzykrucz.transfers.domain.account.AccountNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.CurrencyUnit;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@Getter
@Setter(value = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class OpenAccountCommand {

    private String accountNumber;

    private String currencyCode;

    public AccountNumber getAccountNumber() {
        return new AccountNumber(accountNumber);
    }

    public CurrencyUnit getCurrency() {
        return CurrencyUnit.of(currencyCode);
    }
}
