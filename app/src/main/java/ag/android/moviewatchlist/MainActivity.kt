package ag.android.moviewatchlist

import ag.android.moviewatchlist.ui.FeaturedMoviesScreen
import ag.android.moviewatchlist.ui.MovieDetailsScreen
import ag.android.moviewatchlist.ui.MovieResultsScreen
import ag.android.moviewatchlist.ui.MovieSearchBar
import ag.android.moviewatchlist.ui.theme.MovieWatchlistTheme
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle deep link when app is launched
        // handleAuthRedirect(intent)
        setContent {
            MovieWatchlistTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavGraph(navController = navController, innerPadding = innerPadding)
                }
            }
        }
    }

    // Parse the deep link and finalize login
//    private fun handleAuthRedirect(intent: Intent) {
//        val data = intent.data
//        if (data?.scheme == "moviewatchlist" && data.host == "auth" && data.path == "/callback") {
//            // You can optionally extract params from the deep link if needed
//
//            // Call ViewModel to use the previously stored token to get session ID
//            CoroutineScope(Dispatchers.Main).launch {
//                val viewModel by viewModels<MovieViewModel>()
//                val token = viewModel.tempRequestToken ?: return@launch
//                val sessionId = viewModel.getSessionId(token)
//                viewModel.setSessionId(sessionId)
//                Log.d("DeepLink", "Session ID retrieved: $sessionId")
//            }
//        }
//    }


}

//override fun onNewIntent(intent: Intent?) {
//    super.onNewIntent(intent)
//    intent?.let {
//        handleAuthRedirect(it)
//    }
//}

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

    val popularMovies by viewModel.popularMovies.collectAsState()
    val upcomingMovies by viewModel.upcomingMovies.collectAsState()
    val theaterMovies by viewModel.inTheaters.collectAsState()

    val context = LocalContext.current
    var baseUrl = "https://www.themoviedb.org/authenticate/"

    viewModel.getPopularMovies()
    viewModel.getUpcomingMovies()
    viewModel.getCurrentlyPlaying()

    Scaffold(
        topBar = {
            MovieSearchBar(
                viewModel = viewModel,
                navController = navController,
                movieSearched = movieSearched
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
            ) {

                Column(
                    Modifier.verticalScroll(rememberScrollState())
                ) {
                    FeaturedMoviesScreen("Popular Movies", popularMovies, viewModel, navController)

                    FeaturedMoviesScreen("Coming Soon", upcomingMovies, viewModel, navController)

                    FeaturedMoviesScreen("In Theaters", theaterMovies, viewModel, navController)
                }



//                MovieResultsScreen(
//                    navController,
//                    movieSearchResult,
//                    viewModel
//                )

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            val requestToken = viewModel.getRequestToken()
                            requestToken?.let {
                                val authUrl =
                                    "$baseUrl$requestToken?redirect_to=moviewatchlist://auth/callback"

                                Log.d("REQUEST TOKEN", requestToken.toString())
                                context.startActivity(Intent(Intent.ACTION_VIEW, authUrl.toUri()))

                                val sessionId = viewModel.getSessionId(requestToken)

                                viewModel.setSessionId(sessionId)

                                Log.d("SESSION ID", sessionId.toString())

                            } ?: Log.e("ERROR", "Failed to retrieve Token.")

                        }

                    }
                ) {
                    Text(text = "Login")
                }

            }
        }
    )
}

/*
    TODO:
        - Add sections for Popular, Upcoming, Now Playing, Top Rated in the home screen
            - Uses a Lazy List Row for horizontal scrolling  
        - Rate Movie
            - 1-10? Like/Dislike/Favorite
        - Create list of movies
            - Planning on watching + keeps track of release date
 */