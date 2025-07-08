package com.example.boofilms

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.boofilms.model.Movie


data class FilmItem(
        val title: String,
        val imageUrl: String
    )

    data class FilmCategory(
        val name: String,
        val films: List<FilmItem>
    )

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun FilmCatalogScreen(
        onMenuClick: () -> Unit,
        movies: List<Movie>,
        isLoading: Boolean,
        onBackClick: () -> Unit,
        onAccountClick: () -> Unit,
        onMovieClick: (Movie) -> Unit
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
            return
        }

        val categories = listOf(
            FilmCategory("Рекомендации", movies.map { FilmItem(it.title, it.poster_path ?: "") }),
            FilmCategory("Просмотренное", movies.map { FilmItem(it.title, it.poster_path ?: "") }),
            FilmCategory("Аниме", movies.map { FilmItem(it.title, it.poster_path ?: "") })
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.main),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("BooFilms", color = Color.White)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.Default.ArrowBack, "Назад", tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = onAccountClick) {
                                Icon(Icons.Default.AccountCircle, "Аккаунт", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    categories.forEach { category ->
                        stickyHeader {
                            CategoryHeader(category.name)
                        }

                        item {
                            FilmsHorizontalList(films = category.films)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CategoryHeader(title: String) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 35.dp)
        )
    }

    @Composable
    fun FilmsHorizontalList(films: List<FilmItem>) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(films.size) { index ->
                FilmCard(film = films[index])
            }
        }
    }


@Composable
fun FilmCard(film: FilmItem) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${film.imageUrl}",
                contentDescription = film.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = film.title,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}

    private fun generateFilms(count: Int): List<FilmItem> {
        return List(count) { index ->
            FilmItem(
                title = "Фильм ${index + 1}",
                imageUrl = ""
            )
        }
    }
