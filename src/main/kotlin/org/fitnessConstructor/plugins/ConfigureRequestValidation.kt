package org.fitnessConstructor.plugins

import org.fitnessConstructor.data.models.body.ChangePasswordRequest
import org.fitnessConstructor.data.models.body.ForgetPasswordRequest
import org.fitnessConstructor.data.models.body.LoginRequest
import org.fitnessConstructor.data.models.body.RegisterRequest
import org.fitnessConstructor.data.models.body.ResetRequest
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<LoginRequest> { login ->
            login.validation()
            ValidationResult.Valid
        }
        validate<RegisterRequest> { register ->
            register.validation()
            ValidationResult.Valid
        }
        validate<ChangePasswordRequest> { password ->
            password.validation()
            ValidationResult.Valid
        }
        validate<ResetRequest>{ rq ->
           rq.validation()
            ValidationResult.Valid
        }
        validate<ForgetPasswordRequest>{ rq ->
            rq.validation()
            ValidationResult.Valid
        }

    }
}