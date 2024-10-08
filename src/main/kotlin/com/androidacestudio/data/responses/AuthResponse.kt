package com.androidacestudio.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userName: String? = null,
    val email: String? = null,
    val name: String? = null,
)
