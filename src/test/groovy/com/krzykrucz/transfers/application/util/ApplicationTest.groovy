package com.krzykrucz.transfers.application.util

import com.krzykrucz.transfers.MoneyTransfersApplication
import com.krzykrucz.transfers.application.DomainAPI

class ApplicationTest extends IntegrationTest {

    DomainAPI domainAPI = Mock()

    def setup() {
        app = new TestApplicationWithMockedServices(MoneyTransfersApplication, domainAPI)
    }

    protected
    def "number of times command will fail"(int times, String command, Exception ex = new RuntimeException()) {
        domainAPI."${command}"(_) >>
                { throwExceptionIfNotEnough(1, times, ex) } >>
                { throwExceptionIfNotEnough(2, times, ex) } >>
                { throwExceptionIfNotEnough(3, times, ex) } >>
                { throwExceptionIfNotEnough(4, times, ex) } >>
                { throwExceptionIfNotEnough(5, times, ex) } >>
                { throwExceptionIfNotEnough(6, times, ex) } >>
                { throwExceptionIfNotEnough(7, times, ex) } >>
                { throwExceptionIfNotEnough(8, times, ex) } >>
                { throwExceptionIfNotEnough(9, times, ex) } >>
                { throwExceptionIfNotEnough(10, times, ex) }
    }

    def throwExceptionIfNotEnough(int callNumber, int maxExceptions, Exception exception) {
        if (callNumber > maxExceptions) {
            return
        }
        throw exception
    }

}
