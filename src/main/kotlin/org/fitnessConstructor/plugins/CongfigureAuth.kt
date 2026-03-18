package org.fitnessConstructor.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.fitnessConstructor.data.models.body.JwtTokenRequest
import org.fitnessConstructor.modules.auth.controller.JwtController
import org.slf4j.LoggerFactory

fun Application.configureAuth() {
    install(Authentication) {

        jwt("jwt") {
            provideJwtAuthConfig(this)
        }
    }
}

fun provideJwtAuthConfig(jwtConfig: JWTAuthenticationProvider.Config) {
    val logger = LoggerFactory.getLogger("MyAppLogger")
    logger.info("provideJwtAuthConfig jwtConfig realm: "+jwtConfig.realm)
    logger.info("provideJwtAuthConfig jwtConfig name: "+jwtConfig.name)

    jwtConfig.verifier(JwtController.verifier)
    jwtConfig.realm = "fitnessConstructor"
    jwtConfig.validate {
        logger.info("provideJwtAuthConfig jwtConfig.validate: " )
        val userId = it.payload.getClaim("userId").asString()
        val email = it.payload.getClaim("email").asString()
        logger.info("Token claims: userId=$userId, email=$email")

        JwtTokenRequest(userId, email)
    }
}
