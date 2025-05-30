package ag.android.moviewatchlist

import ag.android.moviewatchlist.ui.MovieDetailsScreen
import ag.android.moviewatchlist.ui.MovieResultsScreen
import ag.android.moviewatchlist.ui.MovieSearchBar
import ag.android.moviewatchlist.ui.theme.MovieWatchlistTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        MovieSearchBar(viewModel = viewModel, movieSearched = movieSearched)

        MovieResultsScreen(
            navController,
            movieSearchResult,
            viewModel
        )

    }
}

/*
    TODO:
        - Rate Movie
            - 1-10? Like/Dislike/Favorite
        - Create list of movies
            - Planning on watching + keeps track of release date
 */