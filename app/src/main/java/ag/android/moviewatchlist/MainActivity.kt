package ag.android.moviewatchlist

import ag.android.moviewatchlist.ui.FeaturedMoviesScreen
import ag.android.moviewatchlist.ui.MovieDetailsScreen
import ag.android.moviewatchlist.ui.MovieResultsScreen
import ag.android.moviewatchlist.ui.MovieSearchBar
import ag.android.moviewatchlist.ui.theme.MovieWatchlistTheme
import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
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

        composable(
            route = "authResult?request_token={request_token}",
            arguments = listOf(navArgument("request_token") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "moviewatchlist://auth/callback?request_token={request_token}"
                })
        ) { backStackEntry ->
            val requestToken = backStackEntry.arguments?.getString("request_token")
            AuthResultScreen(
                requestToken = requestToken,
                onSessionCreated = { navController.navigate("home") },
                viewModel = sharedViewModel
            )
        }
    }
}

@Composable
fun AuthResultScreen(
    requestToken: String?,
    onSessionCreated: () -> Unit,
    viewModel: MovieViewModel
) {
    Log.e("TEST", "WWE MADE IT")

    LaunchedEffect(requestToken) {
        if (requestToken != null) {
            val sessionId = viewModel.requestSessionId(requestToken)
            println(sessionId)
            Log.e("SESSION ID SETTING: ", "$sessionId")
            if (sessionId != null) {
                viewModel.setSessionId(sessionId)
                viewModel.saveSessionId(sessionId)
                viewModel.fetchAccountId(sessionId)
                onSessionCreated()
            } else {
                Log.e("AUTH", "Failed to create Session")
            }
        } else {
            // User denied, handle the error
            Log.e("AUTH", "User denied or token missing")
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
    val baseUrl = "https://www.themoviedb.org/authenticate/"

    viewModel.getPopularMovies()
    viewModel.getUpcomingMovies()
    viewModel.getCurrentlyPlaying()

    // Unit as launchedEffect makes it so that it'll only run once
    LaunchedEffect(Unit) {
        viewModel.getSessionId(context)
        viewModel.fetchAccountId(viewModel.sessionId.value)
    }

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


                MovieResultsScreen(
                    navController,
                    movieSearchResult,
                    viewModel
                )


                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            val requestToken = viewModel.getRequestToken()
                            requestToken?.let {
                                val authUrl =
                                    "$baseUrl$requestToken?redirect_to=moviewatchlist://auth/callback"

                                context.startActivity(Intent(Intent.ACTION_VIEW, authUrl.toUri()))

                            } ?: Log.e("ERROR", "Failed to retrieve Token.")

                        }

                    }
                ) {
                    Text(text = "Login")
                }

                Button(
                    onClick = {
                        Log.d("Account ID TEST: ", viewModel.accountId.value.toString())
                    }
                ) {
                    Text("Test Account ID")
                }
            }
        }
    )
}

/*
    TODO:
        - Favorite Movie
            - Favorite
        - Create list of movies
            - Planning on watching + keeps track of release date
            - When clicking add to watchlist, use the sessionId to be able to add to account's watchlist
 */