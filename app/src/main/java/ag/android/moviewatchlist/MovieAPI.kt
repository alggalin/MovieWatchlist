package ag.android.moviewatchlist

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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

const val BASE_URL = "https://api.themoviedb.org/3"
const val IMAGE_URL = "https://image.tmdb.org/t/p/w500/"

@Module
@InstallIn(SingletonComponent::class)
object MovieModule {
    @Provides
    fun provideMovieAPI(): MovieAPI {
        return MovieAPI
    }
}

object MovieAPI {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
                coerceInputValues = true
            })
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

    suspend fun addToWatchlist(
        sessionId: String,
        accountId: String,
        mediaId: Int
    ): Boolean {

        val requestBody = WatchListRequest(
            mediaId = mediaId,
            mediaType = "movie",
            watchlist = true
        )

        // TODO: Retrieve accountId
        val response: HttpResponse = client.post("$BASE_URL/account/$accountId/watchlist") {
            contentType(ContentType.Application.Json)
            parameter("session_id", sessionId)
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_API_KEY}")
            setBody(requestBody)
        }.body()

        return response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK
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