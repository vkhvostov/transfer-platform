package test.interview

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

class TransferControllerTest {

    companion object {
        private val accounts: ConcurrentHashMap<UUID, Account> = ConcurrentHashMap()
    }

    val appConfig = AppConfig.getInstance(Configurations().properties("test-config.properties"))
    private val accountService: AccountService = AccountService.getInstance(accounts)
    private val transferService: TransferService = TransferService.getInstance(accountService)
    private val transferController: TransferController = TransferController()
    private val gson = Gson()

    @Test
    fun `Successfully transfers money from one account to another`() {
        val tan = "555555"
        val fromAccountCode = UUID.randomUUID()
        val fromAccount = Account(fromAccountCode, "Ryan Sheckler", BigDecimal(500.50), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf(tan))
        accounts[fromAccountCode] = fromAccount
        val toAccountCode = UUID.randomUUID()
        val toAccount = Account(toAccountCode, "Rodney Mullen", BigDecimal(500.30), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf(tan))
        accounts[toAccountCode] = toAccount

        val amount = "400.25"

        val request = "{\n" +
                "\t\"from_account\": \"$fromAccountCode\",\n" +
                "\t\"to_account\": \"$toAccountCode\",\n" +
                "\t\"amount\": $amount,\n" +
                "\t\"currency\": \"USD\",\n" +
                "\t\"TAN\": \"$tan\",\n" +
                "\t\"note\": \"Just a transfer\"\n" +
                "}"

        val response = transferController.transfer(request)

        val type = object : TypeToken<List<Account>>() {}
        val actualAccounts = gson.fromJson<List<Account>>(response.entity.toString(), type.type)

        Assert.assertEquals(Response.Status.OK.statusCode, response.status)
        Assert.assertEquals(2, actualAccounts.size)
        Assert.assertEquals(fromAccountCode, actualAccounts[0].code)
        Assert.assertEquals(fromAccount.balance - BigDecimal(amount), actualAccounts[0].balance)
        Assert.assertEquals(toAccountCode, actualAccounts[1].code)
        Assert.assertEquals(toAccount.balance + BigDecimal(amount), actualAccounts[1].balance)
    }

    @Test
    fun `Unsuccessful attempt to transfers money from one account to another because of wrong TAN`() {
        val tan = "555555"
        val fromAccountCode = UUID.randomUUID()
        val fromAccount = Account(fromAccountCode, "Ryan Sheckler", BigDecimal(500), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf(tan))
        accounts[fromAccountCode] = fromAccount
        val toAccountCode = UUID.randomUUID()
        val toAccount = Account(toAccountCode, "Rodney Mullen", BigDecimal(500), Currency.getInstance("EUR"), AccountStatus.OPEN, listOf(tan))
        accounts[toAccountCode] = toAccount

        val amount = "400"

        val request = "{\n" +
                "\t\"from_account\": \"$fromAccountCode\",\n" +
                "\t\"to_account\": \"$toAccountCode\",\n" +
                "\t\"amount\": $amount,\n" +
                "\t\"currency\": \"USD\",\n" +
                "\t\"TAN\": \"wrong tan\",\n" +
                "\t\"note\": \"Just a transfer\"\n" +
                "}"

        val response = transferController.transfer(request)

        Assert.assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
    }
}