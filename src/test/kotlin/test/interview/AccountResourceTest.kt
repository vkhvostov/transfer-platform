package test.interview

import com.google.gson.Gson
import org.apache.commons.configuration2.builder.fluent.Configurations
import org.junit.Assert
import org.junit.Test
import test.interview.config.AppConfig
import test.interview.model.Account
import test.interview.model.AccountStatus
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.ws.rs.core.Response

/**
 * Created on 19.09.18
 * TODO: Add comment
 */
class AccountResourceTest {

    companion object {
        private val accounts: ConcurrentHashMap<UUID, Account> = ConcurrentHashMap()
    }

    val appConfig = AppConfig.getInstance(Configurations().properties("test-config.properties"))
    private val accountService: AccountService = AccountService.getInstance(accounts)
    private val accountResource: AccountResource = AccountResource()
    private val gson = Gson()

    @Test
    fun `Successfully creates account from account create request`() {
        val holder = "Chris Haslam"
        val balance = 500
        val currency = "EUR"
        val request = "{\n" +
                "\t\"account_holder\" : \"$holder\",\n" +
                "\t\"initial_balance\" : $balance,\n" +
                "\t\"currency\" : \"$currency\"\n" +
                "}"
        val response = accountResource.createAccount(request)

        val actualAccount = gson.fromJson(response.entity.toString(), Account::class.java)

        Assert.assertEquals(Response.Status.OK.statusCode, response.status)
        Assert.assertEquals(holder, actualAccount.holder)
        Assert.assertEquals(BigDecimal(balance), actualAccount.balance)
        Assert.assertEquals(Currency.getInstance(currency), actualAccount.currency)
        Assert.assertEquals(AccountStatus.OPEN, actualAccount.status)
    }

    @Test
    fun `Successfully receive account balance`() {
        val balance = "300"
        val accountCode = UUID.randomUUID()
        val account = Account(accountCode, "Ryan Sheckler", BigDecimal(balance), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf("555555"))
        accounts[accountCode] = account

        val response = accountResource.receiveAccountBalance(accountCode.toString())

        val actualBalance = response.entity

        Assert.assertEquals(Response.Status.OK.statusCode, response.status)
        Assert.assertEquals(balance, actualBalance)
    }

    @Test
    fun `Successfully changes account balance`() {
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
        val response = accountResource.changeAccountBalance(request)

        val actualAccount = gson.fromJson(response.entity.toString(), Account::class.java)

        Assert.assertEquals(Response.Status.OK.statusCode, response.status)
        Assert.assertEquals(holder, actualAccount.holder)
        Assert.assertEquals(BigDecimal(newBalance), actualAccount.balance)
        Assert.assertEquals(currency, actualAccount.currency)
        Assert.assertEquals(AccountStatus.OPEN, actualAccount.status)
    }

    @Test
    fun `Successfully closes account`() {
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
        val response = accountResource.closeAccount(request)

        val actualAccount = gson.fromJson(response.entity.toString(), Account::class.java)

        Assert.assertEquals(Response.Status.OK.statusCode, response.status)
        Assert.assertEquals(holder, actualAccount.holder)
        Assert.assertEquals(balance, actualAccount.balance)
        Assert.assertEquals(currency, actualAccount.currency)
        Assert.assertEquals(AccountStatus.CLOSED, actualAccount.status)
    }
}