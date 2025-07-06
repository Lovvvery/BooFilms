package com.example.boofilms

import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru-RU",
        @Query("page") page: Int = 1
    ): MovieResponse
}