package com.androidacestudio

import io.ktor.server.application.*
import com.androidacestudio.data.user.MongoUserDataSource
import com.androidacestudio.plugins.configureMonitoring
import com.androidacestudio.plugins.configureRouting
import com.androidacestudio.plugins.configureSecurity
import com.androidacestudio.plugins.configureSerialization
import com.androidacestudio.security.hashing.SHA256HashingService
import com.androidacestudio.security.token.JwtTokenService
import com.androidacestudio.security.token.TokenConfig
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

/**
*
* First configure mongo db and setup the connection before running it.
* For more help read this, https://www.mongodb.com/developer/languages/kotlin/mastering-kotlin-creating-api-ktor-mongodb-atlas/
*/
fun Application.module() {
    val mongoPw = MONGO_PW
    val dbName = "" // TODO - pass database name here
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://<username>:<password>@<cluster-url>/<dbname>?retryWrites=true&w=majority" // TODO Configure before testing it.
    ).coroutine
        .getDatabase(dbName)
    val userDataSource = MongoUserDataSource(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expireIn = 365L * 1000L * 60L * 60L * 24L,
        secret = JWT_SECRET
    )

    val hashingService = SHA256HashingService()
    configureSecurity(tokenConfig)
    configureMonitoring()
    configureSerialization()
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig)
}

const val JWT_SECRET = ""  // TODO - Before running pass jwt secret here.
const val MONGO_PW = "" // TODO - Add Mongodb password here.