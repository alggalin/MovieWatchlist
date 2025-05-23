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

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie = _selectedMovie.asStateFlow()


    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
    }

    fun getSelectedMovie(): Movie? {
        return selectedMovie.value
    }
    fun updateMovie(title: String) {
        _movie.value = title
    }

    fun searchMovie(movieTitle: String) {
        viewModelScope.launch {
            val result = repository.searchMovie(movieTitle)
            _searchResult.value = result
        }

        return
    }


    fun validateApi() {
        viewModelScope.launch {
            MovieAPI.validateApiKey()
        }
    }
}