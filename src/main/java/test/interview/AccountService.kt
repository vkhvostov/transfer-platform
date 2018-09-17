package test.interview

import org.apache.logging.log4j.LogManager
import test.interview.config.AppConfig
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
 * Created on 15.09.18
 * TODO: Add comment
 */
object AccountService {

    private val logger = LogManager.getLogger(javaClass)

    private val accounts = ConcurrentHashMap<UUID, Account>()

    // returns null when new account is created
    fun createAccount(createRequest: CreateAccountRequest): UUID {
        val accountCode = UUID.randomUUID()
        val balance = BigDecimal(createRequest.initialBalance)
        val currency = Currency.getInstance(createRequest.currency)
        val account = Account(accountCode, createRequest.accountHolder, balance, currency, AccountStatus.OPEN, generateTANs())

        accounts[accountCode] = account
        return accountCode
    }

    fun receiveBalance(accountCode: String): BigDecimal {
        val accountUUID = UUID.fromString(accountCode)
        return accounts.getOrElse(accountUUID) { throw IllegalArgumentException("Incorrect account code") }.balance
    }

    fun changeBalance(changeBalanceRequest: ChangeBalanceRequest): Account? {
        val newBalance = BigDecimal(changeBalanceRequest.balance)
        return changeBalance(changeBalanceRequest.accountCode, newBalance, changeBalanceRequest.tan)
    }

    fun closeAccount(closeAccountRequest: CloseAccountRequest): Account? {
        return changeAccount(
            closeAccountRequest.accountCode,
            closeAccountRequest.tan
        ) { acc -> acc.copy(status = AccountStatus.CLOSED) }
    }

    fun changeBalance(accountCode: UUID, newBalance: BigDecimal, tan: String): Account? {
        return changeAccount(
            accountCode,
            tan
        ) { acc -> acc.copy(balance = newBalance) }
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
            ThreadLocalRandom.current().nextInt(AppConfig.tanLowerBound, AppConfig.tanHigherBound).toString()
        }.take(AppConfig.tanNumber).toList() + listOf()
}
