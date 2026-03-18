package org.fitnessConstructor.plugins


import org.fitnessConstructor.modules.profile.controller.ProfileController
import org.fitnessConstructor.modules.profile.routes.profileRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.fitnessConstructor.modules.auth.controller.AuthController
import org.fitnessConstructor.modules.auth.routes.authRoutes
import org.koin.ktor.ext.inject
import kotlin.getValue


fun Application.configureRoute() {
    val authController: AuthController by inject()
    val profileController: ProfileController by inject()
    routing {
        authRoutes(authController)
        profileRoutes(profileController)
    }

}