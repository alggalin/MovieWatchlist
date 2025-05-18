package ag.android.moviewatchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val _movie = MutableStateFlow("Click to get a movie")
    val movie = _movie.asStateFlow()

    fun searchMovie() {
        viewModelScope.launch {
            MovieAPI.searchMovie()
        }
    }


    fun validateApi() {
        viewModelScope.launch {
            MovieAPI.validateApiKey()
        }
    }
}