package org.fitnessConstructor.data.models.body

import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotNull
import org.valiktor.validate

data class ForgetPasswordRequest(val email: String) {
    fun validation() {
        validate(this) {
            validate(ForgetPasswordRequest::email).isNotNull().isEmail()
        }
    }
}
