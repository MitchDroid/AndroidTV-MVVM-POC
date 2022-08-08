package com.zemoga.apptvdemo.ui.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zemoga.apptvdemo.util.flow.mutableEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _shouldGoToLogin = mutableEventFlow<Boolean>()
    val shouldGoToLogin = _shouldGoToLogin.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(2500L)
            _shouldGoToLogin.tryEmit(true)
        }
    }
}