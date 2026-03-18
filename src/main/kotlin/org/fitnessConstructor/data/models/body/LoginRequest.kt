package org.fitnessConstructor.data.models.body

import org.fitnessConstructor.utils.AppConstants
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotNull
import org.valiktor.validate

data class LoginRequest(
    val email: String,
    val password: String
) {
    fun validation() {
        validate(this) {
            validate(LoginRequest::email).isNotNull().isEmail()
            validate(LoginRequest::password).isNotNull().hasSize(AppConstants.MIN, AppConstants.MAX)
        }
    }
}
