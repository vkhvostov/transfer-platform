package test.interview

import org.apache.logging.log4j.LogManager
import test.interview.config.SingletonHolder
import test.interview.model.Account
import test.interview.model.MoneyTransferRequest
import test.interview.model.exception.InsufficientFundsException
import java.math.BigDecimal

/**
 * Service responsible for transferring money from one account to another
 */
class TransferService(private val accountService: AccountService) {

    private val logger = LogManager.getLogger(javaClass)

    companion object : SingletonHolder<TransferService, AccountService>(::TransferService)

    fun transfer(transferRequest: MoneyTransferRequest): List<Account?> {
        logger.info("New transfer request for ${transferRequest.amount} ${transferRequest.currency} from ${transferRequest.fromAccount} to ${transferRequest.toAccount}")
        val fromAccount = accountService.findAccount(transferRequest.fromAccount)
        val toAccount = accountService.findAccount(transferRequest.toAccount)

        val fromAccountBalance = fromAccount.balance
        val newFromAccountBalance = fromAccountBalance.subtract(transferRequest.amount)

        if (newFromAccountBalance < BigDecimal.ONE)
            throw InsufficientFundsException("Account ${fromAccount.code} has insufficient funds for this transaction")

        val updatedFromAccount =
            accountService.changeBalance(fromAccount.code, newFromAccountBalance, transferRequest.tan)
        val toAccountBalance = toAccount.balance
        val newToAccountBalance = toAccountBalance.add(transferRequest.amount)
        val updatedToAccount = accountService.changeBalance(
            toAccount.code,
            newToAccountBalance,
            toAccount.tans.first()
        ) // TODO: how to pass tan for to account?

        return listOf(updatedFromAccount, updatedToAccount)
    }
}
