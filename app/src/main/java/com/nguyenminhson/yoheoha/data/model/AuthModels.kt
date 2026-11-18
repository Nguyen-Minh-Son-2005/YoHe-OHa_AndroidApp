package com.nguyenminhson.yoheoha.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("nickname") val nickname: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("nickname") val nickname: String
)

data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("user") val user: UserDto
)
