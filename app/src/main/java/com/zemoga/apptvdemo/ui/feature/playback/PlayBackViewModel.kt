package com.zemoga.apptvdemo.ui.feature.playback

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayBackViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel(), PlaybackStateMachine
{

    private val _args = MutableStateFlow<PlaybackVideoFragmentArgs?>(null)
    val args = _args.asStateFlow()
    private val playbackStateListeners = arrayListOf<PlaybackStateListener>()

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

}