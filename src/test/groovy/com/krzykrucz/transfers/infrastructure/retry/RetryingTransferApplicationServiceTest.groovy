package com.krzykrucz.transfers.infrastructure.retry

import com.krzykrucz.transfers.application.TransfersApplicationService
import com.krzykrucz.transfers.application.api.command.*
import com.krzykrucz.transfers.application.error.OptimisticLockException
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import spock.lang.Specification
import spock.lang.Unroll

class RetryingTransferApplicationServiceTest extends Specification {

    static final TEN_USD = Money.of(CurrencyUnit.USD, 10)

    def regularService = Mock(TransfersApplicationService)

    RetryingTransferApplicationService retryingService = new RetryingTransferApplicationService(regularService)

    @Unroll
    def "should retry all commands"() {
        given:
        2 * regularService."${operation}"(_) >> { throw new OptimisticLockException() } >> {}

        when:
        retryingService."${operation}"(command)

        then:
        noExceptionThrown()

        where:
        operation         | command                           || _
        "depositMoney"    | Stub(DepositMoneyCommand)         || _
        "transfer"        | Stub(PerformMoneyTransferCommand) || _
        "openAccount"     | Stub(OpenAccountCommand)          || _
        "depositMoney"    | Stub(DepositMoneyCommand)         || _
        "acceptTransfer"  | Stub(AcceptTransferCommand)       || _
        "rejectTransfer"  | Stub(RejectTransferCommand)       || _
        "receiveTransfer" | Stub(ReceiveTransferCommand)      || _
    }

}
