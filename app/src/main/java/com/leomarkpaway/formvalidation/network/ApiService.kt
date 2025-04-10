package com.leomarkpaway.formvalidation.network

import com.leomarkpaway.formvalidation.network.model.ApiResponse
import com.leomarkpaway.formvalidation.network.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("3f63a1d4-a708-4d0f-839c-10226e78264f")
    suspend fun submitForm(@Body requestBody: User): Response<ApiResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://mocki.io/v1/"

    val apiService: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}
