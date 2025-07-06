package com.example.boofilms

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val backdrop_path: String?,
    val vote_average: Double,
    val release_date: String,
    val genres: List<Genre>?
)

data class Genre(
    val id: Int,
    val name: String
)

data class MovieResponse(
    val results: List<Movie>
)