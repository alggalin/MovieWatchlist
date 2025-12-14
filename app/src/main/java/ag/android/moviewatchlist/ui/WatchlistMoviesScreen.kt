package ag.android.moviewatchlist.ui

import ag.android.moviewatchlist.Movie
import ag.android.moviewatchlist.MovieViewModel
import ag.android.moviewatchlist.R
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun WatchlistMoviesScreen(viewModel: MovieViewModel, navController: NavController) {
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val watchlistMovies = remember { mutableStateOf<List<Movie>?>(null) }

    LaunchedEffect(Unit) {
        watchlistMovies.value = viewModel.getWatchlistMovies(
            viewModel.accountId.value!!,
            viewModel.getSessionId(context)!!,
            1
        )
    }


    if (watchlistMovies.value?.isNotEmpty() == true) {
        LazyColumn(state = listState) {
            watchlistMovies.value!!.forEach { movie ->
                item {
                    WatchlistMovieCard(movie, viewModel, navController)
                }
            }
        }
    } else if (watchlistMovies.value == null || watchlistMovies.value!!.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Your Watchlist is empty.")

        }
    }
}

@Composable
fun WatchlistMovieCard(movie: Movie, viewModel: MovieViewModel, navController: NavController) {

    val imageUrl = "https://image.tmdb.org/t/p/original${movie.posterPath}"
    val releaseYear: String? = extractYear(movie.releaseDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .height(200.dp),
        shape = RectangleShape,
        onClick = {
            viewModel.selectMovie(movie)
            navController.navigate("details?showSearchBar=false")
        },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                    .build(),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .width(125.dp)
                    .padding(4.dp)
                    .padding(vertical = 8.dp),
                contentDescription = "Poster for ${movie.title}",
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.baseline_image_24),
                error = painterResource(R.drawable.baseline_image_not_supported_24)
            )

            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
            ) {
                if (releaseYear != null) {
                    Text(
                        movie.title + " (${releaseYear})",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp,
                        color = Color.Black
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_star_rate_24),
                        modifier = Modifier.size(18.dp),
                        contentDescription = "Rating"
                    )

                    val movieRating = "%.1f".format(movie.voteAverage)

                    if (movieRating == "0.0") {
                        Text(
                            text = "N/A"
                        )
                    } else {
                        Text(
                            text = "%.1f".format(movie.voteAverage)
                        )

                    }

                    Text(
                        text = "(${movie.voteCount})",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                movie.overview?.let {
                    Text(
                        it, maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}