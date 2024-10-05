package com.androidacestudio.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val name: String,
    val email: String,
    val password: String,
)
