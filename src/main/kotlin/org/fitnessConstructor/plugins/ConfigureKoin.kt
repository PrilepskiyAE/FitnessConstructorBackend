package org.fitnessConstructor.plugins

import io.ktor.server.application.*
import org.fitnessConstructor.di.controllerModule
import org.fitnessConstructor.di.databaseModule
import org.fitnessConstructor.di.serviceModule
import org.koin.core.logger.Level
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger(Level.INFO)
        modules(
            databaseModule,
            serviceModule,
            controllerModule
        )
    }
}