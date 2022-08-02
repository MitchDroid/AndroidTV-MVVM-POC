package com.zemoga.apptvdemo.ui.feature.home

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.leanback.widget.ImageCardView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zemoga.apptvdemo.R
import com.zemoga.apptvdemo.data.local.Category
import com.zemoga.apptvdemo.data.remote.Movie
import com.zemoga.apptvdemo.data.repo.MoviesRepo
import com.zemoga.apptvdemo.ui.feature.detail.DetailFragmentArgs
import com.zemoga.apptvdemo.util.Resource
import com.zemoga.apptvdemo.util.flow.mutableEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val moviesRepo: MoviesRepo
) : ViewModel() {
    private val _moviesResponse =
        MutableStateFlow<Resource<List<Category>>>(Resource.Idle())
    val moviesResponse = _moviesResponse.asStateFlow()

    private val _toast = mutableEventFlow<Int>()
    val toast = _toast.asSharedFlow()

    private val _navigateToDetail = mutableEventFlow<DetailFragmentArgs>()
    val navigateToDetail = _navigateToDetail.asSharedFlow()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            with(_moviesResponse) {
                tryEmit(Resource.Loading())
                tryEmit(Resource.Success(moviesRepo.getMovies().categorize()))
                // TODO : Handle network error
            }
        }
    }

    /**
     * To convert movie list to categorized feed
     */
    private fun List<Movie>.categorize(): List<Category> {
        val genreSet = mutableSetOf<String>()
        for (movie in this) {
            for (genre in movie.genre) {
                genreSet.add(genre)
            }
        }
        val feedItems = mutableListOf<Category>()
        for ((index, genre) in genreSet.withIndex()) {
            val categoryId = index.toLong()
            // TODO: Optimize
            val genreMovies = this.filter { it.genre.contains(genre) }
                .map { movie -> movie.copy().apply { this.categoryId = categoryId } }
                .sortedByDescending { it.year ?: 0 }

            feedItems.add(
                Category(
                    categoryId,
                    genre,
                    genreMovies
                )
            )
        }
        return feedItems
    }

    var scrollPos: Pair<Int, Int>? = null

    fun onMovieClicked(movie: Movie) {
        if (moviesResponse.value is Resource.Success) {
            val categories = (moviesResponse.value as Resource.Success<List<Category>>).data
            val clickedCategory = categories.find { it.id == movie.categoryId }!!

            // Navigating to detail
            _navigateToDetail.tryEmit(
                DetailFragmentArgs(clickedCategory, movie)
            )

            // Find category position and movie position
            val catPos = categories.indexOf(clickedCategory)
            val moviePos = clickedCategory.movies.indexOf(movie)

            scrollPos = Pair(catPos, moviePos)
        }
    }

    fun onSearchClicked(){
        _toast.tryEmit(R.string.implement_search)
    }


    fun resetScrollPos() {
        scrollPos = null
    }
}
