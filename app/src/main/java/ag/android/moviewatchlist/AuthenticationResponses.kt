package ag.android.moviewatchlist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenRequest(
    val success: Boolean,
    @SerialName("expires_at")
    val expiresAt: String,
    @SerialName("request_token")
    val requestToken: String

)

@Serializable
data class TokenResponse(
    @SerialName("request_token")
    val requestToken: String
)

@Serializable
data class SessionRequest(
    @SerialName("request_token")
    val requestToken: String
)

@Serializable
data class SessionResponse(
    val success: Boolean,
    @SerialName("session_id")
    val sessionId: String
)

@Serializable
data class AccountResponse(
    val id: Int,
    val name: String,
    val username: String
)