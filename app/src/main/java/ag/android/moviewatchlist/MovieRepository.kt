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
}