package ag.android.moviewatchlist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

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

@Serializable
data class AccountStates(
    val id: Int,
    val favorite: Boolean,
    val rated: JsonElement?,
    val watchlist: Boolean
)

@Serializable
data class Rated(
    val value: Int
)

data class NewAccountStates(
    val id: Int,
    val favorite: Boolean,
    val rated: Float?,
    val watchlist: Boolean
)

@Serializable
data class RatingRequest(
    val value: Float
)

@Serializable
data class FavoriteRequest(
    @SerialName("media_type")
    val mediaType: String,
    @SerialName("media_id")
    val mediaId: Int,
    val favorite: Boolean
)