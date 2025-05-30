package ag.android.moviewatchlist.ui

import ag.android.moviewatchlist.MovieViewModel
import ag.android.moviewatchlist.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieSearchBar(viewModel: MovieViewModel, movieSearched: String) {

    // State variable for search bar collapse/expansion
    var typing by rememberSaveable { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .semantics { traversalIndex = 0f },
        leadingIcon = {

            if (!typing) {
                Icon(
                    painter = painterResource(R.drawable.baseline_search_24),
                    contentDescription = "Search Icon"
                )
            } else {
                IconButton(
                    onClick = { typing = !typing }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back"
                    )
                }
            }
        },
        trailingIcon = {

            if (movieSearched.isNotEmpty()) {
                IconButton(
                    onClick = {
                        viewModel.updateMovie("")
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_clear_24),
                        contentDescription = "Cancel Icon"
                    )
                }
            }
        },
        query = movieSearched,
        onQueryChange = { viewModel.updateMovie(it) },
        onSearch = {
            typing = false
            viewModel.searchMovie(movieSearched)
        },
        active = typing,
        onActiveChange = {

        },
        placeholder = { Text("Search Movie") }
    ) {

    }
}