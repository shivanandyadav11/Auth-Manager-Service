package com.androidacestudio.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.androidacestudio.authenticate
import com.androidacestudio.data.user.UserDataSource
import com.androidacestudio.getSecretInfo
import com.androidacestudio.security.hashing.HashingService
import com.androidacestudio.signIn
import com.androidacestudio.signUp
import com.androidacestudio.security.token.TokenConfig
import com.androidacestudio.security.token.TokenService

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(hashingService, userDataSource, tokenService, tokenConfig)
        signUp(hashingService, userDataSource, tokenService, tokenConfig)
        authenticate(tokenService, tokenConfig)
        getSecretInfo()
    }
}
