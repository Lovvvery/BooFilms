package com.example.boofilms

class MovieRepository(private val apiService: TmdbApiService) {
    private val apiKey = "8389718597b56455bd1899a698962bf5"

    suspend fun getPopularMovies(): List<Movie> {
        return apiService.getPopularMovies(apiKey).results
    }
}