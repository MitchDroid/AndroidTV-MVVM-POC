package com.zemoga.apptvdemo.ui.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.zemoga.apptvdemo.R
import com.zemoga.apptvdemo.data.remote.Movie
import com.zemoga.apptvdemo.ui.feature.playback.PlaybackVideoFragmentArgs
import com.zemoga.apptvdemo.util.flow.mutableEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _args = MutableStateFlow<DetailFragmentArgs?>(null)
    val args = _args.asStateFlow()

    private val _toast = mutableEventFlow<Int>()
    val toast = _toast.asSharedFlow()

    private val _navigateToDetail = mutableEventFlow<DetailFragmentArgs>()
    val navigateToDetail = _navigateToDetail.asSharedFlow()

    private val _navigateToPlayback = mutableEventFlow<PlaybackVideoFragmentArgs>()
    val navigateToPlayback = _navigateToPlayback.asSharedFlow()

    init {
        val parsedArgs = DetailFragmentArgs.fromSavedStateHandle(savedStateHandle)
        _args.tryEmit(parsedArgs)
    }


    fun onPlayClicked(movie: Movie) {
        //_toast.tryEmit(R.string.detail_tada)
        // Navigating to detail
        _navigateToPlayback.tryEmit(
            PlaybackVideoFragmentArgs(movie)
        )

    }

    fun onRateClicked() {
        _toast.tryEmit(R.string.detail_rate)

    }

    fun onMovieClicked(movie: Movie) {
        _navigateToDetail.tryEmit(DetailFragmentArgs(_args.value!!.category, movie))
    }
}