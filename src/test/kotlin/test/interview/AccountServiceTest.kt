package test.interview

import org.apache.commons.configuration2.builder.fluent.Configurations
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import test.interview.config.AppConfig
import test.interview.model.Account
import test.interview.model.AccountStatus
import test.interview.model.ChangeBalanceRequest
import test.interview.model.CloseAccountRequest
import test.interview.model.CreateAccountRequest
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AccountServiceTest {
    
    private lateinit var accounts: ConcurrentHashMap<UUID, Account>
    private lateinit var accountService: AccountService

    @Before
    fun setUp() {
        AppConfig.getInstance(Configurations().properties("test-config.properties"))
        accounts = ConcurrentHashMap()
        accountService = AccountService(accounts)
    }

    @Test
    fun `Successfully created account`() {
        val accountHolder = "Acc Holder"
        val balance = "500"
        val currency = "EUR"
        val createAccountRequest = CreateAccountRequest(accountHolder, balance, currency)
        val actualAccount = accountService.createAccount(createAccountRequest)

        val expectedAccount = Account(actualAccount.code, accountHolder, BigDecimal(balance), Currency.getInstance(currency), AccountStatus.OPEN, listOf("555555"))

        Assert.assertEquals(expectedAccount, actualAccount)
    }

    @Test
    fun `Successfully received balance`() {
        val balance = BigDecimal(300)
        val accountCode = UUID.randomUUID()
        val account = Account(accountCode, "Holder", balance, Currency.getInstance("EUR"), AccountStatus.OPEN, listOf("555555"))
        accounts[accountCode] = account

        val actualBalance = accountService.receiveBalance(accountCode.toString())

        Assert.assertEquals(balance, actualBalance)
    }

    @Test
    fun `Successfully changed balance`() {
        val accountCode = UUID.randomUUID()
        val tan = "555555"
        val account = Account(accountCode, "Holder", BigDecimal(300), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf(tan))
        accounts[accountCode] = account

        val balance = "500"
        val changeBalanceRequest = ChangeBalanceRequest(accountCode, balance, tan, "Just a transfer")
        val actualAccount = accountService.changeBalance(changeBalanceRequest)
        val expectedAccount = account.copy(balance = BigDecimal(balance))

        Assert.assertEquals(expectedAccount, actualAccount)
    }

    @Test
    fun `Successfully close account`() {
        val accountCode = UUID.randomUUID()
        val tan = "555555"
        val account = Account(accountCode, "Holder", BigDecimal(300), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf(tan))
        accounts[accountCode] = account

        val closeAccountRequest = CloseAccountRequest(accountCode, tan)
        val actualAccount = accountService.closeAccount(closeAccountRequest)
        val expectedAccount = account.copy(status = AccountStatus.CLOSED)

        Assert.assertEquals(expectedAccount, actualAccount)
    }

    @Test
    fun findAccount() {
        val accountCode = UUID.randomUUID()
        val account = Account(accountCode, "Holder", BigDecimal(300), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf("555555"))
        accounts[accountCode] = account

        val actualAccount = accountService.findAccount(accountCode)

        Assert.assertEquals(account, actualAccount)
    }
}