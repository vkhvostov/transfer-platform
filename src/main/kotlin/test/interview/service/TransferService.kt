package test.interview.service

import arrow.core.None
import arrow.core.Option
import org.apache.logging.log4j.LogManager
import test.interview.config.SingletonHolder
import test.interview.model.Account
import test.interview.model.MoneyTransferRequest
import java.math.BigDecimal

/**
 * Service responsible for transferring money from one account to another
 */
class TransferService(private val accountService: AccountService) {

    private val logger = LogManager.getLogger(javaClass)

    companion object : SingletonHolder<TransferService, AccountService>(::TransferService)

    fun transfer(transferRequest: MoneyTransferRequest): Pair<Option<Account>, Option<Account>> {
        logger.info("New transfer request for ${transferRequest.amount} ${transferRequest.currency} from ${transferRequest.fromAccount} to ${transferRequest.toAccount}")
        val fromAccount = accountService.findAccount(transferRequest.fromAccount)
        val toAccount = accountService.findAccount(transferRequest.toAccount)

        val fromAccountBalance = fromAccount.map { it.balance }
        val newFromAccountBalance = fromAccountBalance.map { it - transferRequest.amount }

        if (newFromAccountBalance.exists { it < BigDecimal.ZERO }) {
            logger.error("Account ${fromAccount.map { it.code }} has insufficient funds for this transaction")
            return Pair(None, None)
        }

        val updatedFromAccount = toAccount.flatMap {
            fromAccount.flatMap { acc ->
                newFromAccountBalance.flatMap { balance ->
                    accountService.changeBalance(acc.code, balance, transferRequest.tan)
                }
            }
        }

        val toAccountBalance = toAccount.map { it.balance }
        val newToAccountBalance = toAccountBalance.map { it + transferRequest.amount }

        val updatedToAccount = updatedFromAccount.flatMap {
            toAccount.flatMap { acc ->
                newToAccountBalance.flatMap { balance ->
                    accountService.changeBalance(acc.code, balance, acc.tans.first())
                }
            }
        }

        return Pair(updatedFromAccount, updatedToAccount)
    }
}
