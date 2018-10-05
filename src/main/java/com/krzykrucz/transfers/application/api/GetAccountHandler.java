package com.krzykrucz.transfers.application.api;

import com.krzykrucz.transfers.domain.account.Account;
import com.krzykrucz.transfers.domain.account.AccountNumber;
import com.krzykrucz.transfers.domain.account.AccountRepository;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;
import javax.inject.Singleton;

import static ratpack.jackson.Jackson.json;

@Singleton
public class GetAccountHandler implements Handler {

    private final AccountRepository accountRepository;

    @Inject
    public GetAccountHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        final AccountNumber accountNumber = new AccountNumber(ctx.getPathTokens().get("number"));
        final Account account = accountRepository.findByAccountNumber(accountNumber);
        final AccountInfo accountInfo = new AccountInfo(account);
        ctx.render(json(accountInfo));
    }
}
