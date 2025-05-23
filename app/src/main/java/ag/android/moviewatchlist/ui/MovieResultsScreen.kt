package ag.android.moviewatchlist.ui

import ag.android.moviewatchlist.Movie
import ag.android.moviewatchlist.MovieViewModel
import ag.android.moviewatchlist.R
import ag.android.moviewatchlist.SearchResponse
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// composable for the search results when searching for a movie
@Composable
fun MovieResultsScreen(
    navController: NavController,
    movieResults: SearchResponse?,
    viewModel: MovieViewModel,
    modifier: Modifier
) {
    val listState = rememberLazyListState()

    // "When movieResults changes, run this code"
    LaunchedEffect(movieResults) {
        listState.scrollToItem(0)
    }

    Spacer(modifier = Modifier.size(4.dp))

    LazyColumn(state = listState, modifier = modifier) {
        movieResults?.results?.forEach { movie ->
            item {
                MovieItem(movie, viewModel, navController)
            }
        }
    }
}


// Composable for the layout for the individual movie information
@Composable
fun MovieItem(movie: Movie, viewModel: MovieViewModel, navController: NavController) {

    val imageUrl = "https://image.tmdb.org/t/p/original${movie.posterPath}"
    val releaseYear = extractYear(movie.releaseDate)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        onClick = {
            viewModel.selectMovie(movie)
            navController.navigate("details")
        },
        elevation = CardDefaults.cardElevation(8.dp),
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                    .build(),
                modifier = Modifier
                    .height(250.dp)
                    .width(125.dp)
                    .padding(4.dp),
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
                Text(
                    movie.title + " (${releaseYear})",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 25.sp,
                    color = Color.Black
                )

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

// Extracts YYYY from API YYYY-MM-DD
fun extractYear(movieDate: String?): String? {
    if (movieDate != null) {
        return if (movieDate.length >= 4) movieDate.substring(0, 4) else null
    }

    return null
}
