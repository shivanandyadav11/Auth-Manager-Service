package com.androidacestudio.security.token

interface TokenService {
    fun generate(config: TokenConfig, vararg tokenClaims: TokenClaim): String
}