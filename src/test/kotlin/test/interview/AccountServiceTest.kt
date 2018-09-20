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
import test.interview.model.ChangeBalanceRequest
import test.interview.model.CloseAccountRequest
import test.interview.model.CreateAccountRequest
import test.interview.service.AccountService
import test.interview.storage.InMemoryStorage
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AccountServiceTest {
    
    private lateinit var accounts: ConcurrentHashMap<UUID, Account>
    private lateinit var storage: InMemoryStorage
    private lateinit var accountService: AccountService

    @Before
    fun setUp() {
        AppConfig.getInstance(Configurations().properties("test-config.properties"))
        accounts = ConcurrentHashMap()
        storage = InMemoryStorage(accounts)
        accountService = AccountService(storage)
    }

    @Test
    fun `Successfully created an account`() {
        val accountHolder = "Acc Holder"
        val balance = "500"
        val currency = "EUR"
        val createAccountRequest = CreateAccountRequest(accountHolder, balance, currency)
        val actualAccount = accountService.createAccount(createAccountRequest)

        val expectedAccount = Account(actualAccount.get().code, accountHolder, BigDecimal(balance), Currency.getInstance(currency), AccountStatus.OPEN, listOf("555555"))

        Assert.assertTrue(actualAccount is Some)
        Assert.assertEquals(Some(expectedAccount), actualAccount)
    }

    @Test
    fun `Unsuccessful attempt to create an account due to negative initial balance`() {
        val accountHolder = "Acc Holder"
        val balance = "-500"
        val currency = "EUR"
        val createAccountRequest = CreateAccountRequest(accountHolder, balance, currency)
        val createdAccount = accountService.createAccount(createAccountRequest)

        Assert.assertTrue(createdAccount is None)
    }

    @Test
    fun `Successfully received a balance`() {
        val balance = BigDecimal(300)
        val accountCode = UUID.randomUUID()
        val account = Account(accountCode, "Holder", balance, Currency.getInstance("EUR"), AccountStatus.OPEN, listOf("555555"))
        accounts[accountCode] = account

        val actualBalance = accountService.receiveBalance(accountCode.toString())

        Assert.assertTrue(actualBalance is Some)
        Assert.assertEquals(Some(balance), actualBalance)
    }

    @Test
    fun `Unsuccessful attempt to received balance due to incorrect account code`() {
        val actualBalance = accountService.receiveBalance(UUID.randomUUID().toString())

        Assert.assertTrue(actualBalance is None)
    }

    @Test
    fun `Successfully changed balance`() {
        val accountCode = UUID.randomUUID()
        val tan = "555555"
        val account = Account(accountCode, "Holder", BigDecimal(300), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf(tan))
        accounts[accountCode] = account

        val balance = "500"
        val changeBalanceRequest = ChangeBalanceRequest(accountCode, balance, tan, "Just a change")
        val actualAccount = accountService.changeBalance(changeBalanceRequest)
        val expectedAccount = account.copy(balance = BigDecimal(balance))

        Assert.assertTrue(actualAccount is Some)
        Assert.assertEquals(Some(expectedAccount), actualAccount)
    }

    @Test
    fun `Unsuccessful attempt to changed the balance due to an incorrect account code`() {
        val accountCode = UUID.randomUUID()
        val balance = "500"
        val changeBalanceRequest = ChangeBalanceRequest(accountCode, balance, "", "Just a change")
        val changedBalance = accountService.changeBalance(changeBalanceRequest)

        Assert.assertTrue(changedBalance is None)
    }

    @Test
    fun `Unsuccessful attempt to changed the balance due to a negative balance`() {
        val accountCode = UUID.randomUUID()
        val balance = "-500"
        val changeBalanceRequest = ChangeBalanceRequest(accountCode, balance, "", "Just a change")
        val changedBalance = accountService.changeBalance(changeBalanceRequest)

        Assert.assertTrue(changedBalance is None)
    }

    @Test
    fun `Unsuccessful attempt to changed the balance due to an incorrect TAN`() {
        val accountCode = UUID.randomUUID()
        val tan = "555555"
        val account = Account(accountCode, "Holder", BigDecimal(300), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf(tan))
        accounts[accountCode] = account

        val balance = "500"
        val changeBalanceRequest = ChangeBalanceRequest(accountCode, balance, "", "Just a change")
        val changedBalance = accountService.changeBalance(changeBalanceRequest)

        Assert.assertTrue(changedBalance is None)
    }

    @Test
    fun `Unsuccessful attempt to changed the balance because account is already closed`() {
        val accountCode = UUID.randomUUID()
        val tan = "555555"
        val account = Account(accountCode, "Holder", BigDecimal(300), Currency.getInstance("EUR"), AccountStatus.CLOSED, listOf(tan))
        accounts[accountCode] = account

        val balance = "500"
        val changeBalanceRequest = ChangeBalanceRequest(accountCode, balance, tan, "Just a change")
        val changedBalance = accountService.changeBalance(changeBalanceRequest)

        Assert.assertTrue(changedBalance is None)
    }

    @Test
    fun `Successfully close an account`() {
        val accountCode = UUID.randomUUID()
        val tan = "555555"
        val account = Account(accountCode, "Holder", BigDecimal(300), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf(tan))
        accounts[accountCode] = account

        val closeAccountRequest = CloseAccountRequest(accountCode, tan)
        val actualAccount = accountService.closeAccount(closeAccountRequest)
        val expectedAccount = account.copy(status = AccountStatus.CLOSED)

        Assert.assertTrue(actualAccount is Some)
        Assert.assertEquals(Some(expectedAccount), actualAccount)
    }

    @Test
    fun `Unsuccessful attempt to close an account due to an incorrect account code`() {
        val accountCode = UUID.randomUUID()
        val closeAccountRequest = CloseAccountRequest(accountCode, "")
        val closedAccount = accountService.closeAccount(closeAccountRequest)

        Assert.assertTrue(closedAccount is None)
    }

    @Test
    fun `Successfully finds an account`() {
        val accountCode = UUID.randomUUID()
        val account = Account(accountCode, "Holder", BigDecimal(300), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf("555555"))
        accounts[accountCode] = account

        val actualAccount = accountService.findAccount(accountCode)

        Assert.assertTrue(actualAccount is Some)
        Assert.assertEquals(Some(account), actualAccount)
    }

    @Test
    fun `Unsuccessful attempt to find an account due to incorrect account code`() {
        val accountCode = UUID.randomUUID()
        val findAccount = accountService.findAccount(accountCode)

        Assert.assertTrue(findAccount is None)
    }
}