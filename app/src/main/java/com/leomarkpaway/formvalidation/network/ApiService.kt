package com.leomarkpaway.formvalidation.network

import com.leomarkpaway.formvalidation.network.model.ApiResponse
import com.leomarkpaway.formvalidation.network.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("7f5acbc8-44ff-4f50-a3f4-e28807d0b4ce")
    suspend fun submitForm(): Response<ApiResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://run.mocky.io/v3/"

    val apiService: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}