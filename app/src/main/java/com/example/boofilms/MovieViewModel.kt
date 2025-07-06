package com.example.boofilms

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {
    private val _movies = mutableStateOf<List<Movie>>(emptyList())
    val movies: State<List<Movie>> = _movies

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun loadMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _movies.value = repository.getPopularMovies()
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error loading movies", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}