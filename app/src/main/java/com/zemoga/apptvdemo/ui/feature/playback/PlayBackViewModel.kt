package com.zemoga.apptvdemo.ui.feature.playback

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zemoga.apptvdemo.data.remote.Movie
import com.zemoga.apptvdemo.ui.feature.detail.DetailFragmentArgs
import com.zemoga.apptvdemo.util.flow.mutableEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class PlayBackViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel(), PlaybackStateMachine {

    private val _args = MutableStateFlow<PlaybackVideoFragmentArgs?>(null)
    val args = _args.asStateFlow()
    private val playbackStateListeners = arrayListOf<PlaybackStateListener>()

    private val _navigateToError = mutableEventFlow<PlaybackVideoFragmentArgs>()
    val navigateToError = _navigateToError.asSharedFlow()

    init {
        val parsedArgs = PlaybackVideoFragmentArgs.fromSavedStateHandle(savedStateHandle)
        _args.tryEmit(parsedArgs)
    }


    override fun onStateChange(state: VideoPlaybackState) {
        Timber.d("Playback state machine updated to $state")
        playbackStateListeners.forEach {
            it.onChanged(state)
        }
    }

    /**
     * Adds a [PlaybackStateListener] to be notified of [VideoPlaybackState] changes.
     */
    fun addPlaybackStateListener(listener: PlaybackStateListener) {
        playbackStateListeners.add(listener)
    }

    /**
     * Removes the [PlaybackStateListener] so it receives no further [VideoPlaybackState] changes.
     */
    fun removePlaybackStateListener(listener: PlaybackStateListener) {
        playbackStateListeners.remove(listener)
    }

    override fun onCleared() {
        playbackStateListeners.forEach { it.onDestroy() }
    }


}