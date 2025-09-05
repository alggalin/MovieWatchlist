package ag.android.moviewatchlist.ui

import ag.android.moviewatchlist.MovieViewModel
import ag.android.moviewatchlist.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun MovieDetailsScreen(
    navController: NavController,
    viewModel: MovieViewModel,
    modifier: Modifier
) {
    val movie by viewModel.selectedMovie.collectAsState()
    val releaseYear = extractYear(movie?.releaseDate)
    val imageUrl = "https://image.tmdb.org/t/p/original${movie?.posterPath}"
    val movieSearched by viewModel.movie.collectAsState()

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
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {

                Text(
                    text = "${movie!!.title} ($releaseYear)",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
                    lineHeight = 36.sp,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold

                )

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(4.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
                            .crossfade(true)
                            .build(),
                        modifier = Modifier
                            .height(300.dp)
                            .width(200.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentDescription = "Poster for ${movie!!.title}",
                        contentScale = ContentScale.Fit,
                        placeholder = painterResource(R.drawable.baseline_image_24),
                        error = painterResource(R.drawable.baseline_image_not_supported_24)
                    )

                    Column(
                        modifier = Modifier
                            .height(300.dp)
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_star_rate_24),
                                modifier = Modifier.size(18.dp),
                                contentDescription = "Rating"
                            )

                            val movieRating = "%.1f".format(movie!!.voteAverage)

                            if (movieRating == "0.0") {
                                Text(
                                    text = "N/A"
                                )
                            } else {
                                Text(
                                    text = "%.1f".format(movie!!.voteAverage)
                                )

                            }

                            Text(
                                text = "(${movie!!.voteCount})",
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(),
                            shape = RoundedCornerShape(8.dp),
                            onClick = { //  check if user is logged in, if they are then add to their watchlist
                                // if they're not, request a token to get a sessionId
                                // redirect them with the token to login
                                viewModel.addToWatchlist(
                                    viewModel.sessionId.value!!,
                                    viewModel.accountId.value!!,
                                    movie!!.id
                                )
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_add_24),
                                contentDescription = "Add to Watchlist Button"
                            )
                            Text(
                                "Add to Watchlist",
                                modifier = Modifier.weight(1f),
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                }

                movie!!.overview?.let { Text(text = it) }
            }
        }
    )

}