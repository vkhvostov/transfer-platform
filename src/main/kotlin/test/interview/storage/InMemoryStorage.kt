package test.interview.storage

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import test.interview.model.Account
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * In memory storage build on concurrent hash map
 */
class InMemoryStorage(private val accounts: ConcurrentHashMap<UUID, Account>) : Storage {

    override fun store(accountCode: UUID, account: Account) { accounts[accountCode] = account }

    override fun find(accountCode: UUID): Option<Account> {
        val account = accounts[accountCode]
        return if (account != null) Some(account) else None
    }

    override fun contains(accountCode: UUID): Boolean = accounts.contains(accountCode)

    override fun updateIfPresent(accountCode: UUID, function: (UUID, Account) -> Account?): Option<Account> {
        val account = accounts.computeIfPresent(accountCode, function)
        return if (account != null) Some(account) else None
    }
}
