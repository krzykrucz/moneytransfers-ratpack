package com.krzykrucz.transfers.infrastructure.persistence

import com.google.inject.Inject
import com.google.inject.Provider
import com.krzykrucz.transfers.domain.account.AccountRepository
import com.krzykrucz.transfers.domain.common.DomainEventPublisher
import spock.mock.DetachedMockFactory

@com.google.inject.Singleton
class AccountRepositorySpyProvider implements Provider<AccountRepository> {

    private final static MOCK_FACTORY = new DetachedMockFactory()

    private final InMemoryAccountRepository accountRepository

    @Inject
    AccountRepositorySpyProvider(DomainEventPublisher domainEventPublisher) {
        this.accountRepository = MOCK_FACTORY.Spy(new InMemoryAccountRepository(domainEventPublisher))
    }

    @Override
    AccountRepository get() {
        return accountRepository
    }
}