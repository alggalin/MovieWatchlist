package ag.android.moviewatchlist.ui

import ag.android.moviewatchlist.Movie
import ag.android.moviewatchlist.MovieViewModel
import ag.android.moviewatchlist.R
import ag.android.moviewatchlist.SearchResponse
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest


/*

    Function will need to take the list of movies to display and maybe the name of the section

    "In Theaters, Coming Soon, Popular"

 */

@Composable
fun FeaturedMoviesScreen(
    movieCategory: String,
    featuredMovies: SearchResponse?,
    viewModel: MovieViewModel,
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = movieCategory,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (featuredMovies != null) {
                items(featuredMovies.results) { movie ->

                    FeaturedMovieCard(movie, viewModel, navController)

                }
            }
        }
    }
}

@Composable
fun FeaturedMovieCard(movie: Movie, viewModel: MovieViewModel, navController: NavController) {

    val imageUrl = "https://image.tmdb.org/t/p/original${movie.posterPath}"

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
        modifier = Modifier
            .height(300.dp)
            .width(200.dp)
            .padding(4.dp)
            .clickable {
                viewModel.selectMovie(movie)
                navController.navigate("details")
            }
            .clip(RoundedCornerShape(8.dp)),

        contentDescription = "Poster for ${movie.title}",
        contentScale = ContentScale.Fit,
        placeholder = painterResource(R.drawable.baseline_image_24),
        error = painterResource(R.drawable.baseline_image_not_supported_24)
    )
}