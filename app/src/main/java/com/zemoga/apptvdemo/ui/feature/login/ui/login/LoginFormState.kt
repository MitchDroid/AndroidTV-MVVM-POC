package com.zemoga.apptvdemo.ui.feature.login.ui.login

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data validation state of the login form.
 */
@Parcelize
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
) : Parcelable