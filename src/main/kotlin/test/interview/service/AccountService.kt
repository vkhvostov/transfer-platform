package test.interview.service

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import org.apache.logging.log4j.LogManager
import test.interview.config.AppConfig
import test.interview.config.SingletonHolder
import test.interview.model.Account
import test.interview.model.AccountStatus
import test.interview.model.ChangeBalanceRequest
import test.interview.model.CloseAccountRequest
import test.interview.model.CreateAccountRequest
import test.interview.storage.InMemoryStorage
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * Service responsible for operations with an account
 */
class AccountService(private val accounts: InMemoryStorage) {

    private val logger = LogManager.getLogger(javaClass)

    private val appConfig = AppConfig.getInstance()

    companion object : SingletonHolder<AccountService, InMemoryStorage>(::AccountService)

    fun createAccount(createRequest: CreateAccountRequest): Option<Account> {
        val accountCode = UUID.randomUUID()
        val balance = BigDecimal(createRequest.initialBalance)
        if (balance < BigDecimal.ZERO) return None.also { logger.error("Initial account balance is bellow zero") }
        val currency = Currency.getInstance(createRequest.currency)
        val account = Account(accountCode, createRequest.accountHolder, balance, currency, AccountStatus.OPEN, generateTANs())
        logger.info("Created account: $account")

        accounts.store(accountCode, account)
        return Some(account)
    }

    fun receiveBalance(accountCode: String): Option<BigDecimal> {
        val accountUUID = UUID.fromString(accountCode)
        val account = accounts.find(accountUUID)
        return if (account is Some) Some(account.t.balance) else None
    }

    fun changeBalance(changeBalanceRequest: ChangeBalanceRequest): Option<Account> {
        val newBalance = BigDecimal(changeBalanceRequest.balance)
        return changeBalance(changeBalanceRequest.accountCode, newBalance, changeBalanceRequest.tan)
    }

    fun closeAccount(closeAccountRequest: CloseAccountRequest): Option<Account> {
        return changeAccount(
            closeAccountRequest.accountCode,
            closeAccountRequest.tan
        ) { acc -> acc.copy(status = AccountStatus.CLOSED) }
    }

    fun changeBalance(accountCode: UUID, newBalance: BigDecimal, tan: String): Option<Account> {
        if (newBalance < BigDecimal.ZERO) return None.also { logger.error("New account balance cannot be negative") }
        return changeAccount(
            accountCode,
            tan
        ) { acc -> acc.copy(balance = newBalance) }
    }

    fun findAccount(accountCode: UUID): Option<Account> =
        accounts.find(accountCode)

    private fun changeAccount(accountCode: UUID, tan: String, change: (acc: Account) -> Account): Option<Account> {
        return accounts.updateIfPresent(accountCode) { _, account ->
            val open = account.status == AccountStatus.OPEN
            if (open && account.tans.contains(tan)) {
                val updatedAccount = change(account)
                logger.info("Updated account $updatedAccount")
                updatedAccount
            } else {
                logger.error((if (open) "Incorrect TAN" else "Account is already closed"))
                null
            }
        }
    }

    private fun generateTANs(): List<String> =
        generateSequence {
            ThreadLocalRandom.current().nextInt(appConfig.tanLowerBound, appConfig.tanHigherBound).toString()
        }.take(appConfig.tanNumber).toList()
}
