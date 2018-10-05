package com.krzykrucz.transfers.domain;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public interface CurrencyExchanger {

    Money exchange(Money money, CurrencyUnit currencyUnit);

}
