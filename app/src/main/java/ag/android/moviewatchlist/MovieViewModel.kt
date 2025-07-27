package ag.android.moviewatchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
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

    private var _sessionId = MutableStateFlow<String?>(null)
    val sessionId = _sessionId.asStateFlow()

    private var _accountId = MutableStateFlow<String?>(null)
    val accountId = _accountId.asStateFlow()

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
    }

    fun getSelectedMovie(): Movie? {
        return selectedMovie.value
    }
    fun updateMovie(title: String) {
        _movie.value = title
    }

    fun setSessionId(sessionId: String?) {
        _sessionId.value = sessionId
    }

    fun searchMovie(movieTitle: String) {
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

    fun getUpcomingMovies() {
        viewModelScope.launch {
            _upcomingMovies.value = repository.getUpcomingMovies()
        }
    }
    fun getCurrentlyPlaying() {
        viewModelScope.launch {
            _inTheaters.value = repository.getCurrentlyPlaying()
        }
    }

    fun addToWatchlist(sessionId: String, accountId: Int, mediaId: Int) {
        viewModelScope.launch {
            repository.addToWatchlist(sessionId, accountId, mediaId)
        }
    }


    suspend fun getRequestToken(): String? {
        return repository.getRequestToken()
    }

    suspend fun getSessionId(token: String): String? {
        return repository.getSessionId(token)
    }
}