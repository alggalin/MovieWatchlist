package ag.android.moviewatchlist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchListRequest(
    @SerialName("media_type")
    val mediaType: String,
    @SerialName("media_id")
    val mediaId:Int,
    val watchlist: Boolean
)

@Serializable
data class WatchlistResponse(
    val success: Boolean,
    @SerialName("status_code")
    val statusCode: Int,
    @SerialName("status_message")
    val statusMessage: String
)