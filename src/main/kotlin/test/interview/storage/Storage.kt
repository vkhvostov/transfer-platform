package test.interview.storage

import test.interview.model.Account
import java.util.*

interface Storage {
    fun store(accountCode: UUID, account: Account)
    fun find(accountCode: UUID): Account?
    fun contains(accountCode: UUID): Boolean
    fun computeIfPresent(accountCode: UUID, function: (UUID, Account) -> Account): Account?
}
