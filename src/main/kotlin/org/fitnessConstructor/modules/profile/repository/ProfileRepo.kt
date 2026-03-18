package org.fitnessConstructor.modules.profile.repository

import arrow.core.Either
import org.fitnessConstructor.data.models.body.JwtTokenRequest
import org.fitnessConstructor.data.models.body.UserProfileRequest
import org.fitnessConstructor.data.models.response.ProfileResponse

interface ProfileRepo {

    suspend fun getProfile(userId: String,email: String): Either<String, ProfileResponse>

    suspend fun updateProfile(jwtTokenRequest: JwtTokenRequest, profileRequest: UserProfileRequest): Either<String, String>

    suspend fun removeProfile(userId: String,email: String): Either<String, Boolean>

}