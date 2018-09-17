package test.interview.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.*

/**
 * Created on 15.09.18
 * TODO: Add comment
 */
data class MoneyTransferRequest(
    @SerializedName("from_account") val fromAccount: UUID,
    @SerializedName("to_account") val toAccount: UUID,
    @SerializedName("amount") val amount: BigDecimal,
    @SerializedName("currency") val currency: Currency,
    @SerializedName("TAN") val tan: String,
    @SerializedName("note") val note: String
)