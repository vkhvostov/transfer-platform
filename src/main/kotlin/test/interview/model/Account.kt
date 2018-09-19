package test.interview.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.*

data class Account(
    @SerializedName("code") val code: UUID,
    @SerializedName("holder") val holder: String,
    @SerializedName("balance") val balance: BigDecimal,
    @SerializedName("currency") val currency: Currency,
    @SerializedName("status") val status: AccountStatus,
    @Transient val tans: List<String>
)

enum class AccountStatus {
    OPEN, CLOSED
}
