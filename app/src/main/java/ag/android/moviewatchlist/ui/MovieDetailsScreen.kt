package ag.android.moviewatchlist.ui

import ag.android.moviewatchlist.MovieViewModel
import ag.android.moviewatchlist.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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

    Column(modifier = modifier
        .fillMaxSize()
        .padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)) {

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

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                .build(),
            modifier = Modifier
//                .height(400.dp)
                .width(250.dp)
                .padding(4.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(8.dp)),
            contentDescription = "Poster for ${movie!!.title}",
            contentScale = ContentScale.Fit,
            placeholder = painterResource(R.drawable.baseline_image_24),
            error = painterResource(R.drawable.baseline_image_not_supported_24)
        )

        movie!!.overview?.let { Text(text = it) }
    }
}