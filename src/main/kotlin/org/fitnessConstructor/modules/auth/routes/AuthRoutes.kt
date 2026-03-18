package org.fitnessConstructor.modules.auth.routes

import org.fitnessConstructor.modules.auth.controller.AuthController
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.fitnessConstructor.data.models.body.ChangePasswordRequest
import org.fitnessConstructor.data.models.body.ForgetPasswordRequest
import org.fitnessConstructor.data.models.body.JwtTokenRequest
import org.fitnessConstructor.data.models.body.LoginRequest
import org.fitnessConstructor.data.models.body.RegisterRequest
import org.fitnessConstructor.data.models.body.ResetRequest
import org.fitnessConstructor.utils.ApiResponse
import org.fitnessConstructor.utils.AppConstants
import org.fitnessConstructor.utils.Response
import org.fitnessConstructor.utils.extension.apiResponse
import org.fitnessConstructor.utils.extension.notFoundException
import org.fitnessConstructor.utils.sendEmail


/**
 * Defines authentication routes for login, registration, password reset, and password change.
 *
 * @param authController The controller handling authentication-related operations.
 */
fun Route.authRoutes(authController: AuthController) {
    route("") {

        /**
         * Handles the login request.
         *
         * Receives a [LoginRequest] object and responds with a successful login response.
         */
        post("login", {
            tags("Auth")
            request {
                body<LoginRequest>()
            }
            apiResponse()
        }) {
            val requestBody = call.receive<LoginRequest>()

            val result = authController.login(requestBody)
            result.fold(
                { error ->
                    call.respond(
                        HttpStatusCode.BadRequest, ApiResponse.failure(
                            Response.Alert(error), HttpStatusCode.BadRequest
                        )
                    )
                },
                { token ->

                    call.respond(
                        ApiResponse.success(token, HttpStatusCode.OK)
                    )
                }
            )
        }


        /**
         * Handles the registration request.
         *
         * Receives a [RegisterRequest] object and responds with a successful registration response.
         */
        post("register", {
            tags("Auth")
            request {
                body<RegisterRequest>()
            }
            apiResponse<String>("result: token")
        }) {
            val requestBody = call.receive<RegisterRequest>()
            authController.register(requestBody).fold(ifRight = { token ->
                call.respond(ApiResponse.success(token, HttpStatusCode.OK))
            }, ifLeft = { errorMessage ->
                call.respond(
                    HttpStatusCode.BadRequest, ApiResponse.failure(
                        Response.Alert(errorMessage.message), HttpStatusCode.BadRequest
                    )
                )
            })
        }

        /**
         * Handles the request to change the password for authenticated users.
         *
         * Requires the old and new password as query parameters and responds with a success or failure message.
         */
        authenticate("jwt") {
            put("change-password", {
                tags("Auth")
                request {
                    body<ChangePasswordRequest>()
                }
                apiResponse<String>("result: token")
            }) {
                val body = call.receive<ChangePasswordRequest>()
                val loginUser = call.principal<JwtTokenRequest>() ?: throw "User".notFoundException()
                authController.changePassword(
                    loginUser.userId,
                    ChangePasswordRequest(body.oldPassword, body.newPassword)
                )
                    .onRight { token ->
                        call.respond(
                            ApiResponse.success(
                                token,
                                HttpStatusCode.OK,
                                alert = Response.Alert(message = AppConstants.ErrorMessage.MESSAGE16)
                            )
                        )
                    }
                    .onLeft { errorMessage ->
                        call.respond(
                            HttpStatusCode.BadRequest, ApiResponse.failure(
                                Response.Alert(errorMessage), HttpStatusCode.BadRequest
                            )
                        )
                    }

            }
        }

        /**
         * Handles the request for sending a password reset verification code.
         *
         * Receives the user's email as a query parameter and sends a verification code to the email.
         */
        get("forget-password", {
            tags("Auth")
            request {
                body<ForgetPasswordRequest>()
            }
            apiResponse<String>("result: status and code")
        }) {
            val body = call.receive<ForgetPasswordRequest>()
            authController.forgetPassword(body)
                .onRight { otp ->
                    sendEmail(body.email, otp)
                    call.respond(
                        ApiResponse.success(
                            "${AppConstants.SuccessMessage.VerificationCode.VERIFICATION_CODE_SENT_TO} ${body.email} and code is $otp",
                            HttpStatusCode.OK
                        )
                    )
                }
                .onLeft { errorMessage ->
                    call.respond(
                        HttpStatusCode.BadRequest, ApiResponse.failure(
                            Response.Alert(errorMessage), HttpStatusCode.BadRequest
                        )
                    )
                }
        }

        /**
         * Handles the request for resetting the password.
         *
         * Receives the email, OTP, and new password as query parameters and verifies the password reset code.
         */
        get("reset-password", {
            tags("Auth")
            request {
                body<ResetRequest>()
            }
            apiResponse<String>("result: status")
        }) {
            val body = call.receive<ResetRequest>()

            authController.resetPassword(
                body
            ).onRight {
                when (it) {
                    AppConstants.DataBaseTransaction.FOUND -> {
                        call.respond(
                            ApiResponse.success(
                                AppConstants.SuccessMessage.Password.PASSWORD_CHANGE_SUCCESS, HttpStatusCode.OK
                            )
                        )
                    }

                    AppConstants.DataBaseTransaction.NOT_FOUND -> {
                        call.respond(
                            ApiResponse.success(
                                AppConstants.SuccessMessage.VerificationCode.VERIFICATION_CODE_IS_NOT_VALID,
                                HttpStatusCode.OK
                            )
                        )
                    }
                }
            }.onLeft { errorMessage ->
                call.respond(
                    HttpStatusCode.BadRequest, ApiResponse.failure(
                        Response.Alert(errorMessage), HttpStatusCode.BadRequest
                    )
                )
            }
        }
    }
}