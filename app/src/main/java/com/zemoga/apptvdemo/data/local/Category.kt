package com.zemoga.apptvdemo.data.local

import android.os.Parcelable
import com.zemoga.apptvdemo.data.remote.Movie
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Long,
    val genre: String,
    val movies: List<Movie>
) : Parcelable