package com.zemoga.apptvdemo.ui.feature.playback

import androidx.compose.runtime.MutableState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class PlaybackErrorViewModel @Inject constructor( savedStateHandle: SavedStateHandle
) : ViewModel()
{

    private val _args = MutableStateFlow<PlaybackErrorFragmentArgs?>(null)
    val args = _args.asStateFlow()

    init {
        val parsedArgs = PlaybackErrorFragmentArgs.fromSavedStateHandle(savedStateHandle)
        _args.tryEmit(parsedArgs)
    }
}