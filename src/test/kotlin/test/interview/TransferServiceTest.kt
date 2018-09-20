package test.interview

import arrow.core.None
import arrow.core.Some
import org.apache.commons.configuration2.builder.fluent.Configurations
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import test.interview.config.AppConfig
import test.interview.model.Account
import test.interview.model.AccountStatus
import test.interview.model.MoneyTransferRequest
import test.interview.service.AccountService
import test.interview.service.TransferService
import test.interview.storage.InMemoryStorage
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class TransferServiceTest {

    private lateinit var accounts: ConcurrentHashMap<UUID, Account>
    private lateinit var storage: InMemoryStorage
    private lateinit var accountService: AccountService
    private lateinit var transferService: TransferService

    @Before
    fun setUp() {
        AppConfig.getInstance(Configurations().properties("test-config.properties"))
        accounts = ConcurrentHashMap()
        storage = InMemoryStorage(accounts)
        accountService = AccountService(storage)
        transferService = TransferService(accountService)
    }

    @Test
    fun `Successfully transferring money`() {
        val fromAccountCode = UUID.randomUUID()
        val currency = Currency.getInstance("EUR")
        val balance = BigDecimal(500)
        val tan = "555555"
        val fromAccount = Account(fromAccountCode, "Rodney Mullen", balance, currency, AccountStatus.OPEN, listOf(tan))
        accounts[fromAccountCode] = fromAccount

        val toAccountCode = UUID.randomUUID()
        val toAccount = Account(toAccountCode, "Deawon Song", balance, currency, AccountStatus.OPEN, listOf(tan))
        accounts[toAccountCode] = toAccount

        val transferAmount = BigDecimal(350)
        val moneyTransferRequest =
            MoneyTransferRequest(fromAccountCode, toAccountCode, transferAmount, currency, tan, "Just a transfer")

        val expectedUpdatedAccounts = Pair(
            Some(fromAccount.copy(balance = balance - transferAmount)),
            Some(toAccount.copy(balance = balance + transferAmount))
        )
        val actualUpdatedAccounts = transferService.transfer(moneyTransferRequest)

        Assert.assertTrue(actualUpdatedAccounts.first.isDefined())
        Assert.assertTrue(actualUpdatedAccounts.second.isDefined())
        Assert.assertEquals(expectedUpdatedAccounts, actualUpdatedAccounts)
    }

    @Test
    fun `Unsuccessful attempt to transfer money when sender has not enough funds`() {
        val fromAccountCode = UUID.randomUUID()
        val currency = Currency.getInstance("EUR")
        val balance = BigDecimal(500)
        val tan = "555555"
        val fromAccount = Account(fromAccountCode, "Rodney Mullen", balance, currency, AccountStatus.OPEN, listOf(tan))
        accounts[fromAccountCode] = fromAccount

        val toAccountCode = UUID.randomUUID()
        val toAccount = Account(toAccountCode, "Deawon Song", balance, currency, AccountStatus.OPEN, listOf(tan))
        accounts[toAccountCode] = toAccount

        val transferAmount = BigDecimal(700)
        val moneyTransferRequest =
            MoneyTransferRequest(fromAccountCode, toAccountCode, transferAmount, currency, tan, "Just a transfer")

        val transfer = transferService.transfer(moneyTransferRequest)

        Assert.assertTrue(transfer.first is None )
        Assert.assertTrue(transfer.second is None )
    }

    @Test
    fun `Unsuccessful attempt to transfer money when sender is not exist`() {
        val fromAccountCode = UUID.randomUUID()
        val currency = Currency.getInstance("EUR")
        val balance = BigDecimal(500)
        val tan = "555555"

        val toAccountCode = UUID.randomUUID()
        val toAccount = Account(toAccountCode, "Deawon Song", balance, currency, AccountStatus.OPEN, listOf(tan))
        accounts[toAccountCode] = toAccount

        val transferAmount = BigDecimal(100)
        val moneyTransferRequest =
            MoneyTransferRequest(fromAccountCode, toAccountCode, transferAmount, currency, tan, "Just a transfer")

        val transfer = transferService.transfer(moneyTransferRequest)

        Assert.assertTrue(transfer.first is None )
        Assert.assertTrue(transfer.second is None )
    }

    @Test
    fun `Unsuccessful attempt to transfer money when receiver is not exist`() {
        val fromAccountCode = UUID.randomUUID()
        val currency = Currency.getInstance("EUR")
        val balance = BigDecimal(500)
        val tan = "555555"
        val fromAccount = Account(fromAccountCode, "Rodney Mullen", balance, currency, AccountStatus.OPEN, listOf(tan))
        accounts[fromAccountCode] = fromAccount

        val toAccountCode = UUID.randomUUID()

        val transferAmount = BigDecimal(100)
        val moneyTransferRequest =
            MoneyTransferRequest(fromAccountCode, toAccountCode, transferAmount, currency, tan, "Just a transfer")

        val transfer = transferService.transfer(moneyTransferRequest)

        Assert.assertTrue(transfer.first is None )
        Assert.assertTrue(transfer.second is None )
    }
}