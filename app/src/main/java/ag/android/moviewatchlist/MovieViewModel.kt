package ag.android.moviewatchlist

import ag.android.moviewatchlist.SessionKey.SESSION_ID
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _movie = MutableStateFlow("")
    val movie = _movie.asStateFlow()

    private val _searchResult = MutableStateFlow<SearchResponse?>(null)
    val searchResult: StateFlow<SearchResponse?> = _searchResult.asStateFlow()

    private val _popularMovies = MutableStateFlow<SearchResponse?>(null)
    val popularMovies: StateFlow<SearchResponse?> = _popularMovies.asStateFlow()

    private val _upcomingMovies = MutableStateFlow<SearchResponse?>(null)
    val upcomingMovies: StateFlow<SearchResponse?> = _upcomingMovies.asStateFlow()

    private val _inTheaters = MutableStateFlow<SearchResponse?>(null)
    val inTheaters: StateFlow<SearchResponse?> = _inTheaters.asStateFlow()

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie = _selectedMovie.asStateFlow()

    private val _accountStates = MutableStateFlow<NewAccountStates?>(null)
    val accountStates = _accountStates.asStateFlow()

    private var _sessionId = MutableStateFlow<String?>(null)
    val sessionId = _sessionId.asStateFlow()

    private var _accountId = MutableStateFlow<Int?>(null)
    val accountId = _accountId.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()


    fun saveSessionId(sessionId: String) {
        viewModelScope.launch {
            context.sessionDataStore.edit { prefs ->
                prefs[SessionKey.SESSION_ID] = sessionId
            }
        }
    }

    suspend fun getSessionId(context: Context): String? {
        val prefs = context.sessionDataStore.data.first()
        setSessionId(prefs[SESSION_ID])
        return prefs[SESSION_ID]
    }

    init {
        viewModelScope.launch {
            val id = getSessionId(context)
            _sessionId.value = id
        }
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
    }

    fun getSelectedMovie(): Movie? {
        return selectedMovie.value
    }

    fun getMovieStates(): NewAccountStates? {
        return accountStates.value
    }

    fun updateMovie(title: String) {
        _movie.value = title
    }

    fun setSessionId(sessionId: String?) {
        _sessionId.value = sessionId
    }

    fun searchMovie(movieTitle: String) {

        if (movieTitle == "") {
            _searchResult.value = null
            return
        }

        viewModelScope.launch {
            val result = repository.searchMovie(movieTitle)
            _searchResult.value = result
        }

        return
    }

    fun getPopularMovies() {

        viewModelScope.launch {
            _popularMovies.value = repository.getPopularMovies()

        }

    }

    fun getUpcomingMovies(): SearchResponse? {
        viewModelScope.launch {
            _upcomingMovies.value = repository.getUpcomingMovies()
        }

        return _upcomingMovies.value
    }

    fun getCurrentlyPlaying() {
        viewModelScope.launch {
            _inTheaters.value = repository.getCurrentlyPlaying()
        }
    }

    suspend fun movieAccountStates(movieId: Int, sessionId: String?) {
        viewModelScope.launch {
            val originalState = repository.movieAccountStates(movieId, sessionId)
            val ratingVal = when (val rated = originalState?.rated) {
                is JsonObject -> rated["value"]?.jsonPrimitive?.floatOrNull
                is JsonPrimitive -> null
                else -> null
            }

            _accountStates.value = NewAccountStates(
                id = originalState!!.id,
                favorite = originalState.favorite,
                rated = ratingVal,
                watchlist = originalState.watchlist
            )
        }
    }

    fun toggleWatchlist(
        addingToWatchlist: Boolean,
        sessionId: String,
        accountId: Int,
        mediaId: Int
    ) {
        viewModelScope.launch {

            val success =
                repository.toggleWatchlist(addingToWatchlist, sessionId, accountId, mediaId)

            if (success) {
                _accountStates.value = accountStates.value?.copy(
                    watchlist = !accountStates.value!!.watchlist
                )

                if(addingToWatchlist) {
                    _uiEvent.emit("Added to watchlist")
                } else {
                    _uiEvent.emit("Removed from watchlist")
                }
            } else {
                // TODO: Show error
            }
        }
    }

    fun toggleFavorite(movieId: Int, currentlyFavorite: Boolean) {
        viewModelScope.launch {
            val success =
                repository.toggleFavorite(movieId, accountId.value, sessionId.value, currentlyFavorite)

            if(success) {
                _accountStates.value = accountStates.value?.copy(
                    favorite = !accountStates.value!!.favorite
                )

                if(currentlyFavorite) {
                    _uiEvent.emit("Removed from favorites")
                } else {
                    _uiEvent.emit("Added to favorites")
                }
            } else {
                // TODO: Show error
            }
        }
    }

    fun rateMovie(movieId: Int, movieRating: Float) {
        viewModelScope.launch {
            val success = repository.rateMovie(movieId, movieRating)

            if (success) {
                _accountStates.value = accountStates.value?.copy(
                    rated = movieRating
                )
                _uiEvent.emit("Rating Saved!")
            } else {
                // TODO: Show error
                _uiEvent.emit("Failed to save rating")
            }
        }
    }


    fun deleteRating(movieId: Int) {
        viewModelScope.launch {
            val success = repository.deleteRating(movieId)

            if (success) {
                _accountStates.value = accountStates.value?.copy(
                    rated = null
                )
                _uiEvent.emit("Rating Deleted")
            } else {
                // TODO: Show error
                _uiEvent.emit("Failed to delete rating")
            }
        }
    }

    suspend fun getRequestToken(): String? {
        return repository.getRequestToken()
    }

    suspend fun requestSessionId(token: String): String? {
        return repository.getSessionId(token)
    }

    suspend fun fetchAccountId(sessionId: String?): Int {
        val response = repository.fetchAccountId(sessionId)
        _accountId.value = response.id
        return response.id
    }
}