package org.fitnessConstructor.data.models.body

import org.fitnessConstructor.utils.AppConstants
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotNull
import org.valiktor.validate

data class ChangePasswordRequest(val oldPassword: String, val newPassword: String) {
    fun validation() {
        validate(this) {
            validate(ChangePasswordRequest::oldPassword).isNotNull().hasSize(AppConstants.MIN, AppConstants.MAX)
            validate(ChangePasswordRequest::newPassword).isNotNull().hasSize(AppConstants.MIN, AppConstants.MAX)
        }
    }
}
