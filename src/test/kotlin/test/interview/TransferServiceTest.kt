package test.interview

import org.apache.commons.configuration2.builder.fluent.Configurations
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import test.interview.config.AppConfig
import test.interview.model.Account
import test.interview.model.AccountStatus
import test.interview.model.MoneyTransferRequest
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class TransferServiceTest {

    private lateinit var accounts: ConcurrentHashMap<UUID, Account>
    private lateinit var accountService: AccountService
    private lateinit var transferService: TransferService

    @Before
    fun setUp() {
        AppConfig.getInstance(Configurations().properties("test-config.properties"))
        accounts = ConcurrentHashMap()
        accountService = AccountService(accounts)
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
        val toAccount = Account(toAccountCode, "Deawon Song", balance, currency, AccountStatus.OPEN, listOf(tan)) // TODO: Make list of tans empty
        accounts[toAccountCode] = toAccount

        val transferAmount = BigDecimal(350)
        val moneyTransferRequest =
            MoneyTransferRequest(fromAccountCode, toAccountCode, transferAmount, currency, tan, "Just a transfer")

        val expectedUpdatedAccounts = listOf(
            fromAccount.copy(balance = balance - transferAmount),
            toAccount.copy(balance = balance + transferAmount)
        )
        val actualUpdatedAccounts = transferService.transfer(moneyTransferRequest)

        Assert.assertTrue(expectedUpdatedAccounts.containsAll(actualUpdatedAccounts))
    }
}