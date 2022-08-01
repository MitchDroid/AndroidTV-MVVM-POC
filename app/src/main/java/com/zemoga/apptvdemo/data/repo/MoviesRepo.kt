package com.zemoga.apptvdemo.data.repo

import com.zemoga.apptvdemo.data.remote.ApiInterface
import javax.inject.Inject

class MoviesRepo @Inject constructor(
    private val apiInterface: ApiInterface
) : ApiInterface by apiInterface