package test.interview.storage

import test.interview.model.Account
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * In memory storage build on concurrent hash map
 */
class InMemoryStorage(private val accounts: ConcurrentHashMap<UUID, Account>) : Storage {

    override fun store(accountCode: UUID, account: Account) { accounts[accountCode] = account }

    override fun find(accountCode: UUID): Account? = accounts[accountCode]

    override fun contains(accountCode: UUID): Boolean = accounts.contains(accountCode)

    override fun computeIfPresent(accountCode: UUID, function: (UUID, Account) -> Account): Account? =
        accounts.computeIfPresent(accountCode, function)
}
