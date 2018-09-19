package test.interview

import org.apache.logging.log4j.LogManager
import test.interview.config.AppConfig
import test.interview.config.SingletonHolder
import test.interview.model.Account
import test.interview.model.AccountStatus
import test.interview.model.ChangeBalanceRequest
import test.interview.model.CloseAccountRequest
import test.interview.model.CreateAccountRequest
import test.interview.model.exception.AccountNotFoundException
import test.interview.model.exception.IllegalOperation
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

/**
 * Service responsible for operations with an account
 */
class AccountService(private val accounts: ConcurrentHashMap<UUID, Account>) {

    private val logger = LogManager.getLogger(javaClass)

    private val appConfig = AppConfig.getInstance()

    companion object : SingletonHolder<AccountService, ConcurrentHashMap<UUID, Account>>(::AccountService)

    fun createAccount(createRequest: CreateAccountRequest): Account {
        val accountCode = UUID.randomUUID()
        val balance = BigDecimal(createRequest.initialBalance)
        if (balance < BigDecimal.ZERO) throw IllegalOperation("Initial account balance is bellow zero")
        val currency = Currency.getInstance(createRequest.currency)
        val account = Account(accountCode, createRequest.accountHolder, balance, currency, AccountStatus.OPEN, generateTANs())
        logger.info("Created account: $account")

        accounts[accountCode] = account
        return account
    }

    fun receiveBalance(accountCode: String): BigDecimal {
        val accountUUID = UUID.fromString(accountCode)
        return accounts.getOrElse(accountUUID) { throw AccountNotFoundException("Incorrect account code") }.balance
    }

    fun changeBalance(changeBalanceRequest: ChangeBalanceRequest): Account {
        val newBalance = BigDecimal(changeBalanceRequest.balance)
        return changeBalance(changeBalanceRequest.accountCode, newBalance, changeBalanceRequest.tan)
    }

    fun closeAccount(closeAccountRequest: CloseAccountRequest): Account {
        return changeAccount(
            closeAccountRequest.accountCode,
            closeAccountRequest.tan
        ) { acc -> acc.copy(status = AccountStatus.CLOSED) }
        ?: throw AccountNotFoundException("Incorrect account code")
    }

    fun changeBalance(accountCode: UUID, newBalance: BigDecimal, tan: String): Account {
        if (newBalance < BigDecimal.ZERO) throw IllegalOperation("New account balance cannot be negative")
        return changeAccount(
            accountCode,
            tan
        ) { acc -> acc.copy(balance = newBalance) }
        ?: throw AccountNotFoundException("Incorrect account code")
    }

    fun findAccount(accountCode: UUID): Account =
        accounts.getOrElse(accountCode) { throw AccountNotFoundException("Account for code $accountCode is not found") }

    private fun changeAccount(accountCode: UUID, tan: String, change: (acc: Account) -> Account): Account? {
        return accounts.computeIfPresent(accountCode) {_, account ->
            val open = account.status == AccountStatus.OPEN
            if (open && account.tans.contains(tan)) {
                change(account)
            } else {
                throw IllegalOperation(if (open) "Incorrect TAN" else "Account is already closed")
            }
        }
    }

    private fun generateTANs(): List<String> =
        generateSequence {
            ThreadLocalRandom.current().nextInt(appConfig.tanLowerBound, appConfig.tanHigherBound).toString()
        }.take(appConfig.tanNumber).toList()
}
