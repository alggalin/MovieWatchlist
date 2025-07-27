package ag.android.moviewatchlist

import javax.inject.Inject


/*
    @Inject = Tells Hilt to automatically provide this repository when it's needed
 */
class MovieRepository @Inject constructor(
    private val api: MovieAPI
) {
    suspend fun searchMovie(movieTitle: String): SearchResponse {
        return api.searchMovie(movieTitle)
    }

    suspend fun getUpcomingMovies(): SearchResponse {
        return api.upcomingMovies()
    }

    suspend fun getPopularMovies(): SearchResponse {
        return api.popularMovies()
    }

    suspend fun getCurrentlyPlaying(): SearchResponse {
        return  api.currentlyPlaying()
    }

    suspend fun addToWatchlist(sessionId: String, accountId: Int, mediaId: Int) {
        api.addToWatchlist(sessionId, accountId, mediaId)
    }

    suspend fun getRequestToken(): String? {
        return api.getRequestToken()
    }

    suspend fun getSessionId(token: String): String? {
        return api.getSessionId(token)
    }
}