package ag.android.moviewatchlist.ui

import ag.android.moviewatchlist.MovieViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MovieDetailsScreen(
    navController: NavController,
    viewModel: MovieViewModel,
    modifier: Modifier
) {
    val movie by viewModel.selectedMovie.collectAsState()

    Column(modifier = modifier) {
        Text(text = movie!!.title)

        movie!!.overview?.let { Text(text = it) }
    }
}