package ag.android.moviewatchlist

import ag.android.moviewatchlist.ui.MovieDetailsScreen
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieWatchlistTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavGraph(navController = navController, innerPadding = innerPadding)
                }
            }
        }
    }
}

@Composable
fun MainNavGraph(navController: NavHostController, innerPadding: PaddingValues) {
    // Shared Viewmodel defined at NavHost level
    val sharedViewModel: MovieViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = sharedViewModel,
                innerPadding = innerPadding

                )
        }

        composable("details") {
            MovieDetailsScreen(
                navController = navController,
                viewModel = sharedViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: MovieViewModel,
    innerPadding: PaddingValues
) {
    val movieSearched by viewModel.movie.collectAsState()
    val movieSearchResult by viewModel.searchResult.collectAsState()
    val selectedMovie by viewModel.selectedMovie.collectAsState()

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

                if (!expanded) {
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

                if (expanded && movieSearched.isNotEmpty()) {
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
            onSearch = { viewModel.searchMovie(movieSearched) }, // TODO: FIX THE SEARCH BAR NOT CLOSING WHEN CLICKING A RESULT
            active = expanded,
            onActiveChange = {
                expanded = !expanded
                viewModel.updateMovie("")
            },
            placeholder = { Text("Search Movie") }
        ) {
            MovieResultsScreen(
                navController,
                movieSearchResult,
                viewModel,
                modifier = Modifier.padding(innerPadding)
            )
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