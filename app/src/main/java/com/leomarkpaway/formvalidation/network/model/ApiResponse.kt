package com.leomarkpaway.formvalidation.network.model

data class ApiResponse(
    val status: String,
    val message: String,
    val user: User?
)

data class User(
    val fullName: String,
    val email: String,
    val mobile: String,
    val dateOfBirth: String,
    val age: Int,
    val gender: String
)