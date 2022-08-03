package com.zemoga.apptvdemo.ui.feature.login.ui.login

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Authentication result : success (user details) or error message.
 */
@Parcelize
data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
) : Parcelable