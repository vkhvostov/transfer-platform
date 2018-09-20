package test.interview.storage

import arrow.core.Option
import test.interview.model.Account
import java.util.*

interface Storage {
    fun store(accountCode: UUID, account: Account)
    fun find(accountCode: UUID): Option<Account>
    fun contains(accountCode: UUID): Boolean
    fun updateIfPresent(accountCode: UUID, function: (UUID, Account) -> Account?): Option<Account>
}
