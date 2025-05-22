package ag.android.moviewatchlist

import ag.android.moviewatchlist.ui.MovieResultsScreen
import ag.android.moviewatchlist.ui.theme.MovieWatchlistTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.exp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieWatchlistTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MovieScreen(innerPadding)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(innerPadding: PaddingValues, viewModel: MovieViewModel = viewModel()) {
    val movie by viewModel.movie.collectAsState()
    val testMovie by viewModel.searchResult.collectAsState()

    // State variable for search bar collapse/expansion
    var expanded by rememberSaveable { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .semantics { traversalIndex = 0f },
            leadingIcon = {

                if(!expanded) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_search_24),
                        contentDescription = "Search Icon"
                    )
                } else {
                    IconButton(
                        onClick = { expanded = !expanded }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                }
            },
            trailingIcon = {

                if (expanded && movie.isNotEmpty()) {
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
            query = movie,
            onQueryChange = { viewModel.updateMovie(it) },
            onSearch = { viewModel.searchMovie(movie) },
            active = expanded,
            onActiveChange = {
                expanded = !expanded
                viewModel.updateMovie("")
            },
            placeholder = { Text("Search Movie") }
        ) {
            MovieResultsScreen(testMovie, modifier = Modifier.padding(innerPadding))
        }


    }
}

/*
    TODO:
        - Rate Movie
            - 1-10? Like/Dislike/Favorite
        - Create list of movies
            - Planning on watching + keeps track of release date
 */