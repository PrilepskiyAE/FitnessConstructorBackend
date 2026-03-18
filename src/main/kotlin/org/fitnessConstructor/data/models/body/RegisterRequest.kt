package org.fitnessConstructor.data.models.body

import org.fitnessConstructor.utils.AppConstants
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotNull
import org.valiktor.validate

data class RegisterRequest(
    val name: String,
    val email: String,
    val country: String,
    val host: String,
    val password: String
) {
    fun validation() {
        validate(this) {
            validate(RegisterRequest::email).isNotNull().isEmail()
            validate(RegisterRequest::password).isNotNull().hasSize(AppConstants.MIN, AppConstants.MAX)
            validate(RegisterRequest::name).isNotNull()
            validate(RegisterRequest::country).isNotNull()
            validate(RegisterRequest::host).isNotNull()
        }
    }
}