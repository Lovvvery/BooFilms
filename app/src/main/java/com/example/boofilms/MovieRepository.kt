package com.example.boofilms.repository

import com.example.boofilms.model.Movie
import com.example.boofilms.network.RetrofitInstance

class MovieRepository {

    private val apiKey = "8389718597b56455bd1899a698962bf5"

    suspend fun getAllMovies(): List<Movie> {
        val popular = RetrofitInstance.api.getPopularMovies(apiKey).results
        val topRated = RetrofitInstance.api.getTopRatedMovies(apiKey).results
        val nowPlaying = RetrofitInstance.api.getNowPlayingMovies(apiKey).results

        return popular + topRated + nowPlaying
    }
}
