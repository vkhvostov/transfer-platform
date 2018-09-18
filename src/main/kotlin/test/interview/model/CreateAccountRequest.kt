package test.interview.model

import com.google.gson.annotations.SerializedName

/**
 * Created on 15.09.18
 * TODO: Add comment
 */
data class CreateAccountRequest(
    @SerializedName("account_holder") val accountHolder: String,
    @SerializedName("initial_balance") val initialBalance: String,
    @SerializedName("currency") val currency: String
)