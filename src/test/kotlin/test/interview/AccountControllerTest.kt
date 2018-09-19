package test.interview

import com.google.gson.Gson
import org.apache.commons.configuration2.builder.fluent.Configurations
import org.junit.Assert
import org.junit.Test
import test.interview.config.AppConfig
import test.interview.controller.AccountController
import test.interview.model.Account
import test.interview.model.AccountStatus
import test.interview.service.AccountService
import test.interview.storage.InMemoryStorage
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.ws.rs.core.Response

class AccountControllerTest {

    companion object {
        private val accounts: ConcurrentHashMap<UUID, Account> = ConcurrentHashMap()
        private val storage: InMemoryStorage = InMemoryStorage(accounts)
    }

    val appConfig = AppConfig.getInstance(Configurations().properties("test-config.properties"))
    private val accountService: AccountService = AccountService.getInstance(storage)
    private val accountController: AccountController =
        AccountController()
    private val gson = Gson()

    @Test
    fun `Successfully creates an account from an account create request`() {
        val holder = "Chris Haslam"
        val balance = 500
        val currency = "EUR"
        val request = "{\n" +
                "\t\"account_holder\" : \"$holder\",\n" +
                "\t\"initial_balance\" : $balance,\n" +
                "\t\"currency\" : \"$currency\"\n" +
                "}"
        val response = accountController.createAccount(request)

        val actualAccount = gson.fromJson(response.entity.toString(), Account::class.java)

        Assert.assertEquals(Response.Status.OK.statusCode, response.status)
        Assert.assertEquals(holder, actualAccount.holder)
        Assert.assertEquals(BigDecimal(balance), actualAccount.balance)
        Assert.assertEquals(Currency.getInstance(currency), actualAccount.currency)
        Assert.assertEquals(AccountStatus.OPEN, actualAccount.status)
    }

    @Test
    fun `Unsuccessful attempt to create an account due to an incorrect currency code`() {
        val holder = "Chris Haslam"
        val balance = 500
        val currency = "XYZ"
        val request = "{\n" +
                "\t\"account_holder\" : \"$holder\",\n" +
                "\t\"initial_balance\" : $balance,\n" +
                "\t\"currency\" : \"$currency\"\n" +
                "}"
        val response = accountController.createAccount(request)

        Assert.assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
    }

    @Test
    fun `Successfully receives an account balance`() {
        val balance = "300"
        val accountCode = UUID.randomUUID()
        val account = Account(accountCode, "Ryan Sheckler", BigDecimal(balance), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf("555555"))
        accounts[accountCode] = account

        val response = accountController.receiveAccountBalance(accountCode.toString())

        val actualBalance = response.entity

        Assert.assertEquals(Response.Status.OK.statusCode, response.status)
        Assert.assertEquals(balance, actualBalance)
    }

    @Test
    fun `Unsuccessfully attempt to receive ab account balance due to an incorrect account code`() {
        val accountCode = UUID.randomUUID()
        val response = accountController.receiveAccountBalance(accountCode.toString())

        Assert.assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
    }

    @Test
    fun `Successfully changes an account balance`() {
        val accountCode = UUID.randomUUID()
        val holder = "Ryan Sheckler"
        val currency = Currency.getInstance("EUR")
        val tan = "555555"
        val account = Account(accountCode, holder, BigDecimal("300"), currency, AccountStatus.OPEN, listOf(tan))
        accounts[accountCode] = account

        val newBalance = "200"
        val request = "{\n" +
                "\t\"account_code\" : \"$accountCode\",\n" +
                "\t\"balance\" : $newBalance,\n" +
                "\t\"TAN\" : \"$tan\",\n" +
                "\t\"note\" : \"Just a balance change\"\n" +
                "}"
        val response = accountController.changeAccountBalance(request)

        val actualAccount = gson.fromJson(response.entity.toString(), Account::class.java)

        Assert.assertEquals(Response.Status.OK.statusCode, response.status)
        Assert.assertEquals(holder, actualAccount.holder)
        Assert.assertEquals(BigDecimal(newBalance), actualAccount.balance)
        Assert.assertEquals(currency, actualAccount.currency)
        Assert.assertEquals(AccountStatus.OPEN, actualAccount.status)
    }

    @Test
    fun `Unsuccessful attempt to change an account balance due to a incorrect account code`() {
        val accountCode = UUID.randomUUID()
        val tan = "555555"

        val newBalance = "200"
        val request = "{\n" +
                "\t\"account_code\" : \"$accountCode\",\n" +
                "\t\"balance\" : $newBalance,\n" +
                "\t\"TAN\" : \"$tan\",\n" +
                "\t\"note\" : \"Just a balance change\"\n" +
                "}"
        val response = accountController.changeAccountBalance(request)

        Assert.assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
    }

    @Test
    fun `Successfully closes an account`() {
        val accountCode = UUID.randomUUID()
        val holder = "Ryan Sheckler"
        val currency = Currency.getInstance("EUR")
        val tan = "555555"
        val balance = BigDecimal("300")
        val account = Account(accountCode, holder, balance, currency, AccountStatus.OPEN, listOf(tan))
        accounts[accountCode] = account

        val request = "{\n" +
                "\t\"account_code\" : \"$accountCode\",\n" +
                "\t\"TAN\" : \"$tan\"\n" +
                "}"
        val response = accountController.closeAccount(request)

        val actualAccount = gson.fromJson(response.entity.toString(), Account::class.java)

        Assert.assertEquals(Response.Status.OK.statusCode, response.status)
        Assert.assertEquals(holder, actualAccount.holder)
        Assert.assertEquals(balance, actualAccount.balance)
        Assert.assertEquals(currency, actualAccount.currency)
        Assert.assertEquals(AccountStatus.CLOSED, actualAccount.status)
    }

    @Test
    fun `Unsuccessful attempt to close an account due to incorrect account code`() {
        val accountCode = UUID.randomUUID()
        val tan = "555555"

        val request = "{\n" +
                "\t\"account_code\" : \"$accountCode\",\n" +
                "\t\"TAN\" : \"$tan\"\n" +
                "}"
        val response = accountController.closeAccount(request)

        Assert.assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
    }

    @Test
    fun `Unsuccessful attempt to call API due to malformed syntax`() {
        val request = "{ }"
        val response = accountController.createAccount(request)

        Assert.assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
    }
}