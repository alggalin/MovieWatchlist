package ag.android.moviewatchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val _movie = MutableStateFlow("")
    val movie = _movie.asStateFlow()

    private val _searchResult = MutableStateFlow<SearchResponse?>(null)
    val searchResult: StateFlow<SearchResponse?> = _searchResult.asStateFlow()

    fun updateMovie(title: String) {
        _movie.value = title
    }

    fun searchMovie(movieTitle: String) {
        viewModelScope.launch {
            val result = MovieAPI.searchMovie(movieTitle)
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