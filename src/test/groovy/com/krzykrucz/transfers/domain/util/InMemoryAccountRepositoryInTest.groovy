package com.krzykrucz.transfers.domain.util

import com.krzykrucz.transfers.domain.common.DomainEventPublisher
import com.krzykrucz.transfers.infrastructure.persistence.InMemoryAccountRepository

class InMemoryAccountRepositoryInTest extends InMemoryAccountRepository {

    InMemoryAccountRepositoryInTest(DomainEventPublisher eventPublisher) {
        super(eventPublisher)
    }

    def clear() {
        super.accountsByNumber.clear()
        super.accountsByTransfer.clear()
    }
}
