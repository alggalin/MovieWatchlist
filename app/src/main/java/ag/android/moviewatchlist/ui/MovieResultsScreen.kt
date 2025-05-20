package ag.android.moviewatchlist.ui

import ag.android.moviewatchlist.Movie
import ag.android.moviewatchlist.SearchResponse
import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// composable for the search results when searching for a movie
@Composable
fun MovieResultsScreen(movieResults: SearchResponse?) {
    val listState = rememberLazyListState()

    // "When movieResults changes, run this code"
    LaunchedEffect(movieResults) {
        listState.scrollToItem(0)
    }

    LazyColumn(state = listState) {
        movieResults?.results?.forEach { movie ->
            item {
                MovieItem(movie)
            }
        }
    }
}


// Composable for the layout for the individual movie information
@Composable
fun MovieItem(movie: Movie) {

    val imageUrl = "https://image.tmdb.org/t/p/original${movie.posterPath}"
    val releaseYear = extractYear(movie.releaseDate)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        onClick = { /* TODO: Navigate to specific movie screen */ },
        //border = BorderStroke(1.dp, color = Color.Black),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                modifier = Modifier
                    .height(250.dp)
                    .padding(4.dp),
                model = imageUrl,
                contentDescription = "Poster for ${movie.title}",
                contentScale = ContentScale.Fit
            )

            Column {
                Text(
                    movie.title + " (${releaseYear})",
                    modifier = Modifier.padding(4.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 25.sp,
                    color = Color.Black
                )

                movie.overview?.let {
                    Text(
                        it, modifier = Modifier.padding(4.dp),
                        maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// Extracts year from API YYYY-MM-DD format
fun extractYear(movieDate: String?): String? {
    if (movieDate != null) {
        return if(movieDate.length >= 4) movieDate.substring(0, 4) else null
    }

    return null
}


private val mockMovies = listOf(
    Movie(
        id = 1,
        title = "Godzilla Minus One",
        overview = "Post-war Japan is attacked by a new kaiju threat.",
        adult = null,
        backdropPath = null,
        genreIds = null,
        originalLanguage = null,
        originalTitle = null,
        popularity = null,
        posterPath = "/pu6twJJq87vzYvtu0OftcN0AkNU.jpg",
        releaseDate = null,
        video = null,
        voteAverage = null,
        voteCount = null
    ),
    Movie(
        id = 2,
        title = "Shin Godzilla",
        overview = "A strange creature emerges in Tokyo Bay...",
        adult = null,
        backdropPath = null,
        genreIds = null,
        originalLanguage = null,
        originalTitle = null,
        popularity = null,
        posterPath = "/9nyToPG5mhfm4KiPGChu35jH9QZ.jpg",
        releaseDate = null,
        video = null,
        voteAverage = null,
        voteCount = null
    )
)

private val mockSearchResponse = SearchResponse(results = mockMovies)

@Preview(showBackground = true)
@Composable
fun PreviewMovieItems() {
    MovieResultsScreen(movieResults = mockSearchResponse)
}