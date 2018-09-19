package test.interview.model.exception

/**
 * Ugly exceptions
 */
class AccountNotFoundException(message: String) : Exception(message)

class IllegalOperation(message: String) : Exception(message)

class InsufficientFundsException(message: String) : Exception(message)
