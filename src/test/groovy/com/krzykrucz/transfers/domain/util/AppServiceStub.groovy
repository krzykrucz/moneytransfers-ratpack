package com.krzykrucz.transfers.domain.util

import com.krzykrucz.transfers.application.DomainAPI
import com.krzykrucz.transfers.application.TransfersApplicationService

class AppServiceStub implements TransfersApplicationService {
    @Delegate
    private final DomainAPI domainAPI

    AppServiceStub(DomainAPI domainAPI) {
        this.domainAPI = domainAPI
    }
}