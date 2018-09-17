package test.interview.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created on 15.09.18
 * TODO: Add comment
 */
data class CloseAccountRequest(
    @SerializedName("account_code") val accountCode: UUID,
    @SerializedName("TAN") val tan: String
)
