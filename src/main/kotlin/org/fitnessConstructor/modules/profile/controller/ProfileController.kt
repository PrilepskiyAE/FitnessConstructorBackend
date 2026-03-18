package org.fitnessConstructor.modules.profile.controller

import am.sig_2.api.data.entities.user.UserDocument
import org.fitnessConstructor.modules.auth.controller.JwtController
import org.fitnessConstructor.modules.profile.repository.ProfileRepo
import arrow.core.Either
import org.fitnessConstructor.data.models.body.JwtTokenRequest
import org.fitnessConstructor.data.models.body.UserProfileRequest
import org.fitnessConstructor.data.models.response.ProfileResponse
import org.fitnessConstructor.data.service.profile.ProfileService
import org.fitnessConstructor.data.service.user.UserService
import org.fitnessConstructor.utils.AppConstants

class ProfileController(private val service: ProfileService, private val authService: UserService) : ProfileRepo {
    override suspend fun getProfile(userId: String, email: String): Either<String, ProfileResponse> {
        val user = authService.findUser(userId).getOrNull()
        return if (user?.email == email.trim().lowercase()) {
            when (val result = service.findProfileEmail(email)) {
                is Either.Left -> Either.Left(result.value.message ?: "")
                is Either.Right -> Either.Right(result.value.toResponse())
            }
        } else {
            Either.Left(AppConstants.ErrorMessage.MESSAGE17)
        }
    }


    override suspend fun updateProfile(
        jwtTokenRequest: JwtTokenRequest,
        profileRequest: UserProfileRequest
    ): Either<String, String> {
        val user = authService.findUser(jwtTokenRequest.userId).getOrNull()
            ?: return Either.Left(AppConstants.ErrorMessage.MESSAGE9)
        val updateUser = authService.update(user.copy(email = profileRequest.email, username = profileRequest.name))
        val updateUserResult: UserDocument =
            updateUser.getOrNull() ?: return Either.Left(AppConstants.ErrorMessage.MESSAGE17)
        return when (val result = service.findProfileEmail(jwtTokenRequest.email)) {
            is Either.Left -> Either.Left(result.value.message ?: AppConstants.ErrorMessage.MESSAGE17)
            is Either.Right -> {
                val profile = service.update(profileRequest.toDocument(result.value.profileId)).getOrNull()
                if (profile != null) {
                    val token =
                        JwtController.tokenProvider(JwtTokenRequest(updateUserResult.userId, updateUserResult.email))
                    Either.Right(token)
                } else {
                    Either.Left(AppConstants.ErrorMessage.MESSAGE17)
                }

            }
        }
    }

    override suspend fun removeProfile(userId: String, email: String): Either<String, Boolean> {
        val user = authService.findUser(userId).getOrNull() ?: return Either.Left(AppConstants.ErrorMessage.MESSAGE9)
        if (user.email != email.trim().lowercase()) return Either.Left(AppConstants.ErrorMessage.MESSAGE17)

        return when (val result = service.findProfileEmail(email)) {
            is Either.Left -> Either.Left(result.value.message ?: AppConstants.ErrorMessage.MESSAGE17)
            is Either.Right -> {
                authService.delete(userId)
                val result = service.delete(result.value.profileId).isRight()
                Either.Right(result)
            }
        }
    }


}