package org.fitnessConstructor.modules.auth.repository

import arrow.core.Either
import org.fitnessConstructor.data.models.body.ChangePasswordRequest
import org.fitnessConstructor.data.models.body.ForgetPasswordRequest
import org.fitnessConstructor.data.models.body.LoginRequest
import org.fitnessConstructor.data.models.body.RegisterRequest
import org.fitnessConstructor.data.models.body.ResetRequest
import org.fitnessConstructor.utils.CommonException

interface AuthRepo {
    /**
     * Registers a new user.
     *
     * @param request The registration details.
     * @return The registration response.
     */
    suspend fun register(registerRequest: RegisterRequest): Either<CommonException, String>

    /**
     * Authenticates a user and returns a login response.
     *
     * @param request The login credentials.
     * @return The login response.
     */
    suspend fun login(loginRequest: LoginRequest): Either<String, String>

    /**
     * Changes the password for a user.
     *
     * @param userId The unique identifier of the user.
     * @param request The password change details.
     * @return `true` if the password change was successful, `false` otherwise.
     */
    suspend fun changePassword(userId: String, changePassword: ChangePasswordRequest): Either<String, String>

    /**
     * Sends a verification code for password reset.
     *
     * @param request The request containing user details.
     * @return The verification code sent to the user.
     */
    suspend fun forgetPassword(forgetPasswordRequest: ForgetPasswordRequest): Either<String, String>

    /**
     * Verifies the password reset code.
     *
     * @param request The request containing the verification details.
     * @return A status code representing the verification result.
     */
    suspend fun resetPassword(resetPasswordRequest: ResetRequest): Either<String, Int>
}