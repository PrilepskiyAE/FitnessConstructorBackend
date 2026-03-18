package org.fitnessConstructor.data.models.body

import org.fitnessConstructor.data.entities.profile.ProfileDocument
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotNull
import org.valiktor.validate

data class UserProfileRequest(
    val name: String,
    val email: String,
    val country: String,
    val host: String,
) {
    fun validation() {
        validate(this) {
            validate(UserProfileRequest::name).isNotNull()
            validate(UserProfileRequest::email).isNotNull().isEmail()
            validate(UserProfileRequest::country).isNotNull()
            validate(UserProfileRequest::host).isNotNull()
        }
    }

    fun toDocument(id: String): ProfileDocument = ProfileDocument(
        profileId = id,
        name = name,
        email = email,
        country = country,
        host = host
    )
}