package org.fitnessConstructor.modules.profile.routes

import org.fitnessConstructor.modules.profile.controller.ProfileController
import arrow.core.Either
import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.fitnessConstructor.data.models.body.JwtTokenRequest
import org.fitnessConstructor.data.models.body.UserProfileRequest
import org.fitnessConstructor.data.models.response.ProfileResponse
import org.fitnessConstructor.utils.ApiResponse
import org.fitnessConstructor.utils.Response
import org.fitnessConstructor.utils.extension.apiResponse
import org.fitnessConstructor.utils.extension.notFoundException

fun Route.profileRoutes(userProfileController: ProfileController) {
    route("profile") {
        authenticate("jwt") {
            get({
                tags("Profile")
                request {
                    queryParameter<String>("email") {
                        required = true
                    }
                }
                apiResponse<ProfileResponse>("Profile")
            }) {
                val loginUser = call.principal<JwtTokenRequest>() ?: throw "User".notFoundException()
                val email = call.parameters["email"] ?: return@get
                when (val result = userProfileController.getProfile(userId = loginUser.userId, email = email)) {
                    is Either.Left -> {
                        call.respond(
                            HttpStatusCode.BadRequest, ApiResponse.failure(
                                Response.Alert(result.value), HttpStatusCode.BadRequest
                            )
                        )
                    }

                    is Either.Right -> {
                        call.respond(
                            ApiResponse.success(
                                result.value, HttpStatusCode.OK
                            )
                        )
                    }
                }
            }
        }

        authenticate("jwt") {
            post({
                tags("Profile")
                request {
                    body<UserProfileRequest>()
                }
                apiResponse<String>("result: update token")
            }) {
                val loginUser = call.principal<JwtTokenRequest>() ?: throw "User".notFoundException()
                val body = call.receive<UserProfileRequest>()
                when (val result = userProfileController.updateProfile(loginUser, body)) {
                    is Either.Left -> {
                        call.respond(
                            HttpStatusCode.BadRequest, ApiResponse.failure(
                                Response.Alert(result.value), HttpStatusCode.BadRequest
                            )
                        )
                    }

                    is Either.Right -> {
                        call.respond(
                            ApiResponse.success(
                                result.value, HttpStatusCode.OK
                            )
                        )
                    }
                }
            }
        }

        authenticate("jwt") {
            delete({
                tags("Profile")
                request {
                    queryParameter<String>("email") {
                        required = true
                    }
                }
                apiResponse<Boolean>("result: status")
            }) {
                val loginUser = call.principal<JwtTokenRequest>() ?: throw "User".notFoundException()
                val email = call.parameters["email"] ?: return@delete
                when (val result = userProfileController.removeProfile(email = email, userId = loginUser.email)) {
                    is Either.Left -> {
                        call.respond(
                            HttpStatusCode.BadRequest, ApiResponse.failure(
                                Response.Alert(result.value), HttpStatusCode.BadRequest
                            )
                        )
                    }

                    is Either.Right -> {
                        call.respond(
                            ApiResponse.success(
                                result.value, HttpStatusCode.OK
                            )
                        )
                    }
                }
            }
        }
    }
}