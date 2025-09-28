package ag.android.moviewatchlist

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject

const val BASE_URL = "https://api.themoviedb.org/3"


@Module
@InstallIn(SingletonComponent::class)
object MovieModule {
    @Provides
    fun provideMovieAPI(): MovieAPI = MovieAPI()
}

class MovieAPI @Inject constructor() {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
                coerceInputValues = true
            })
        }
    }

    /*
        Retrieve request token that will be used to login a user

        Will need to redirect user to:

            https://www.themoviedb.org/authenticate/{REQUEST_TOKEN}?redirect_to=myapp://auth

        "myapp://auth" can be replaced with where we want user to be redirected into app after login
     */

    suspend fun getRequestToken(): String? {

        return try {
            val tokenResponse = client.get("$BASE_URL/authentication/token/new") {
                header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
                contentType(ContentType.Application.Json)
            }

            val requestToken = tokenResponse.body<TokenRequest>().requestToken

            requestToken

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    /*
        Retrieves sessionId that will be used to make actions through the linked account

     */
    suspend fun getSessionId(token: String): String? {

        return try {
            val sessionResponse = client.post("$BASE_URL/authentication/session/new") {
                header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
                contentType(ContentType.Application.Json)
                setBody(SessionRequest(token))
            }
            Log.d("SESSION RAW", sessionResponse.bodyAsText())

            sessionResponse.body<SessionResponse>().sessionId

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    suspend fun searchMovie(movieTitle: String): SearchResponse {
        val response: SearchResponse = client.get("$BASE_URL/search/movie") {
            // parameters
            parameter("query", movieTitle)

            // headers (API Key)
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
        }.body<SearchResponse>()

        if (response.results.isNotEmpty()) {
            println("Movie Found.")
        } else {
            println("No results found!")
        }

        return response
    }

    suspend fun popularMovies(): SearchResponse {
        val response: SearchResponse = client.get("$BASE_URL/movie/popular") {
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
        }.body<SearchResponse>()

        return response
    }

    suspend fun upcomingMovies(): SearchResponse {
        val response: SearchResponse = client.get("$BASE_URL/movie/upcoming") {
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
        }.body<SearchResponse>()

        return response
    }

    suspend fun currentlyPlaying(): SearchResponse {
        val response: SearchResponse = client.get("$BASE_URL/movie/now_playing") {
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
        }.body<SearchResponse>()

        return response
    }

    suspend fun movieAccountStates(movieId: Int, sessionId: String?): AccountStates? {

        if (sessionId == null) {
            return null
        }

        val response: AccountStates = client.get("$BASE_URL/movie/$movieId/account_states") {
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
            parameter("session_id", sessionId)
        }.body<AccountStates>()

        return response
    }

    suspend fun toggleWatchlist(
        addingToWatchlist: Boolean, sessionId: String, accountId: Int, mediaId: Int
    ): Boolean {

        val requestBody = WatchListRequest(
            mediaId = mediaId, mediaType = "movie", watchlist = addingToWatchlist
        )


        val response: HttpResponse = client.post("$BASE_URL/account/$accountId/watchlist") {
            contentType(ContentType.Application.Json)
            parameter("session_id", sessionId)
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
            setBody(requestBody)
        }.body()

        return response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK
    }

    suspend fun favoriteMovieToggle(
        movieId: Int,
        accountId: Int?,
        sessionId: String?,
        favoriteToggle: Boolean
    ): Boolean {
        if (accountId != null) {
            val requestBody = FavoriteRequest("movie", movieId, !favoriteToggle)

            val response: HttpResponse = client.post("$BASE_URL/account/$accountId/favorite") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
                parameter("session_id", sessionId)
                setBody(requestBody)
            }

            return response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created
        }

        return false
    }

    suspend fun rateMovie(movieId: Int, movieRating: Float): Boolean {

        val requestBody = RatingRequest(movieRating)

        val response: HttpResponse = client.post("$BASE_URL/movie/$movieId/rating") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
            setBody(requestBody)
        }

        return response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created
    }

    suspend fun deleteRating(movieId: Int): Boolean {

        val response: HttpResponse = client.delete("$BASE_URL/movie/$movieId/rating") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
        }

        return response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created
    }

    suspend fun fetchAccountId(sessionId: String?): AccountResponse {
        val response: AccountResponse = client.get("$BASE_URL/account") {
            parameter("session_id", sessionId)
            parameter("api_key", BuildConfig.TMDB_API_KEY)
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
        }.body()

        return response
    }

    suspend fun validateApiKey() {
        val client = HttpClient()
        val response: HttpResponse = client.get("https://api.themoviedb.org/3/authentication") {
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
        }
        println(response.status)
        println(response.bodyAsText())
        client.close()
    }

}