package org.fitnessConstructor

import org.fitnessConstructor.plugins.configureAuth
import org.fitnessConstructor.plugins.configureBasic
import org.fitnessConstructor.plugins.configureKoin
import org.fitnessConstructor.plugins.configureMongoDatabase
import org.fitnessConstructor.plugins.configureRequestValidation
import org.fitnessConstructor.plugins.configureRoute
import org.fitnessConstructor.plugins.configureStatusPage
import org.fitnessConstructor.plugins.configureSwagger
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("MyAppLogger")
    logger.info("main")
    embeddedServer(Netty, port = 8080, host = "0.0.0.0"){
        configureMongoDatabase()
        configureBasic()
        configureKoin()
        configureRequestValidation()
         configureAuth()
         configureSwagger()
         configureRoute()
         configureStatusPage()
    }
        .start(wait = true)
}

