package test.interview.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.*

/**
 * Request data classes for transfer platform
 */
data class ChangeBalanceRequest(
    @SerializedName("account_code") val accountCode: UUID,
    @SerializedName("balance") val balance: String,
    @SerializedName("TAN") val tan: String,
    @SerializedName("note") val note: String
)

data class CloseAccountRequest(
    @SerializedName("account_code") val accountCode: UUID,
    @SerializedName("TAN") val tan: String
)

data class CreateAccountRequest(
    @SerializedName("account_holder") val accountHolder: String,
    @SerializedName("initial_balance") val initialBalance: String,
    @SerializedName("currency") val currency: String
)

data class MoneyTransferRequest(
    @SerializedName("from_account") val fromAccount: UUID,
    @SerializedName("to_account") val toAccount: UUID,
    @SerializedName("amount") val amount: BigDecimal,
    @SerializedName("currency") val currency: Currency,
    @SerializedName("TAN") val tan: String,
    @SerializedName("note") val note: String
)