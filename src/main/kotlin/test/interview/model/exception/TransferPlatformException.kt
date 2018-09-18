package test.interview.model.exception

/**
 * Created on 16.09.18
 * TODO: Add comment
 */
class AccountNotFoundException(message: String) : Exception(message)

class IllegalOperation(message: String) : Exception(message)

class InsufficientFundsException(message: String) : Exception(message)