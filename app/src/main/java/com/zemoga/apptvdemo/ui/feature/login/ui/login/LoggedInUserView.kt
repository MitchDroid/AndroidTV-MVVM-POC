package com.zemoga.apptvdemo.ui.feature.login.ui.login

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * User details post authentication that is exposed to the UI
 */
@Parcelize
data class LoggedInUserView(
    val displayName: String
    //... other data fields that may be accessible to the UI
) : Parcelable