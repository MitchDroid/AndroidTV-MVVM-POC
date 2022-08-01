package com.zemoga.apptvdemo.data.remote

import retrofit2.http.GET

interface ApiInterface {
    @GET("top250_min.json")
    suspend fun getMovies(): List<Movie>
}