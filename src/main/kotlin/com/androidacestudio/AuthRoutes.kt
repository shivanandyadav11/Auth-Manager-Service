package com.androidacestudio

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import com.androidacestudio.data.requests.AuthRequest
import com.androidacestudio.data.responses.AuthResponse
import com.androidacestudio.data.user.User
import com.androidacestudio.data.user.UserDataSource
import com.androidacestudio.security.hashing.HashingService
import com.androidacestudio.security.hashing.SaltedHash
import com.androidacestudio.security.token.TokenClaim
import com.androidacestudio.security.token.TokenConfig
import com.androidacestudio.security.token.TokenService

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signup") {
        val request = kotlin.runCatching { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldBlank = request.name.isBlank() || request.email.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 8
        if (areFieldBlank || isPwTooShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val saltedHash = hashingService.generateSaltHash(request.password)
        val user = User(
            name = request.name,
            email = request.email,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val wasAcknowledged = userDataSource.insertNewUser(user)
        if (wasAcknowledged.not()) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            ),
            TokenClaim(
                name = "email",
                value = user.email
            )

        )

        call.respond(
            status = HttpStatusCode.OK, message = AuthResponse(
                token = token,
                userName = user.id.toString(),
                email = user.email
            )
        )
    }
}

fun Route.signIn(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = kotlin.runCatching { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorrect UserName or Password")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt,
            )
        )

        if (isValidPassword.not()) {
            call.respond(HttpStatusCode.Conflict, "Incorrect password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            ),
            TokenClaim(
                name = "email",
                value = user.email
            )

        )

        call.respond(
            status = HttpStatusCode.OK, message = AuthResponse(
                token = token,
                userName = user.id.toString(),
                email = user.email,
                name = user.name,
            )
        )
    }
}

fun Route.authenticate(tokenService: TokenService, tokenConfig: TokenConfig) {
    authenticate {
        get("authenticate") {
            val principal = call.principal<JWTPrincipal>()
            val email = principal?.getClaim("email", String::class)
            val userId = principal?.getClaim("userId", String::class)
            val token = tokenService.generate(
                config = tokenConfig,
                TokenClaim(
                    name = "userId",
                    value = userId.orEmpty()
                ),
                TokenClaim(
                    name = "email",
                    value = email.orEmpty()
                )

            )
            call.respond(
                status = HttpStatusCode.OK, message = AuthResponse(
                    token = token
                )
            )
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your UserId is $userId")
        }
    }
}