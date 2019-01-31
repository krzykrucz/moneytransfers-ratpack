package com.krzykrucz.transfers.application.api.command;

import com.krzykrucz.transfers.domain.account.AccountNumber;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.money.CurrencyUnit;

@AllArgsConstructor
@Data // todo private setters
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
