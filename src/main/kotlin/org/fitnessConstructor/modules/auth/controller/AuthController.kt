package org.fitnessConstructor.modules.auth.controller

import am.sig_2.api.data.entities.user.UserDocument
import org.fitnessConstructor.modules.auth.repository.AuthRepo
import arrow.core.Either
import at.favre.lib.crypto.bcrypt.BCrypt
import org.fitnessConstructor.data.entities.profile.ProfileDocument
import org.fitnessConstructor.data.models.body.ChangePasswordRequest
import org.fitnessConstructor.data.models.body.ForgetPasswordRequest
import org.fitnessConstructor.data.models.body.JwtTokenRequest
import org.fitnessConstructor.data.models.body.LoginRequest
import org.fitnessConstructor.data.models.body.RegisterRequest
import org.fitnessConstructor.data.models.body.ResetRequest
import org.fitnessConstructor.data.service.profile.ProfileService
import org.fitnessConstructor.data.service.user.UserService
import org.fitnessConstructor.utils.AppConstants
import org.fitnessConstructor.utils.CommonException
import org.fitnessConstructor.utils.UserNotExistException
import org.fitnessConstructor.utils.generateOTP

class AuthController(private val service: UserService, private val pService: ProfileService) : AuthRepo {

    /**
     * Registers a new user with the given [registerRequest].
     *
     * The function performs the following steps:
     * 1. Hashes the provided password using BCrypt (12 rounds).
     * 2. Attempts to create a user document in the database.
     * 3. If successful, generates a JWT token for the newly created user.
     *
     * @param registerRequest The request containing user details (name, email, password).
     * @return Either an error wrapped in [CommonException] (e.g., if user already exists or DB operation failed)
     *         or a JWT token string for the registered user.
     *
     * @throws CommonException if:
     *         - a user with the specified email already exists;
     *         - database operation failed;
     *         - any other business/technical error occurred.
     */
    override suspend fun register(registerRequest: RegisterRequest): Either<CommonException, String> {
        val hashedPassword = BCrypt.withDefaults()
            .hashToString(12, registerRequest.password.toCharArray())
        return service.create(
            UserDocument(
                username = registerRequest.name,
                email = registerRequest.email,
                password = hashedPassword,
                otpCode = ""
            )
        ).fold(ifRight = { userDocument ->
            pService.create(
                ProfileDocument(
                    name = registerRequest.name,
                    email = registerRequest.email,
                    country = registerRequest.country,
                    host = registerRequest.host
                )
            ).fold(
                ifLeft = {errorMessage ->
                    Either.Left(errorMessage)
                },
                ifRight = {
                    Either.Right(JwtController.tokenProvider(JwtTokenRequest(userDocument.userId, userDocument.email)))
                }
            )
        }, ifLeft = { errorMessage ->
            Either.Left(errorMessage)
        })
    }


    /**
     * Authenticates a user with the given [loginRequest] credentials.
     *
     * The function performs the following steps:
     * 1. Attempts to find a user by email using [service.findUserEmail].
     * 2. If the user is found, verifies the provided password against the stored BCrypt hash.
     * 3. If both checks pass, generates and returns a JWT authentication token.
     *
     * @param loginRequest The request containing login credentials (email and password).
     * @return Either an error message (as [String]) in case of:
     *         - user not found (email doesn't exist);
     *         - password mismatch;
     *         - BCrypt verification failure or other technical error;
     *         or a JWT token string (successful authentication).
     *
     * Note:
     * - The function does NOT throw exceptions — errors are wrapped in [Either.Left].
     * - Password verification uses BCrypt for secure comparison with the hashed password stored in the database.
     */
    override suspend fun login(loginRequest: LoginRequest): Either<String, String> {
        return when (val userResult = service.findUserEmail(loginRequest.email)) {
            is Either.Right -> {
                val user = userResult.value
                try {
                    val isPasswordValid = BCrypt.verifyer()
                        .verify(loginRequest.password.toCharArray(), user.password)
                        .verified

                    if (isPasswordValid) {
                        val token = JwtController.tokenProvider(JwtTokenRequest(user.userId, user.email))
                        Either.Right(token)
                    } else {
                        Either.Left("Password does not match")
                    }
                } catch (e: Exception) {
                    Either.Left("Password verification failed: ${e.message}")
                }
            }

            is Either.Left -> {
                println("DEBUG: User not found or error: ${userResult.value.message}")
                when (val exception = userResult.value) {
                    is UserNotExistException -> Either.Left("User with email ${loginRequest.email} not found")
                    else -> Either.Left("Authentication error: ${exception.message}")
                }
            }
        }
    }


    /**
     * Changes the password for a user with the given [userId] and [changePassword] request.
     *
     * The function performs the following steps:
     * 1. Finds the user by [userId] using [service.findUser].
     * 2. Verifies that the provided old password matches the stored BCrypt hash.
     * 3. Checks that the new password is different from the old one.
     * 4. Hashes the new password using BCrypt (12 rounds).
     * 5. Updates the user document in the database with the new hashed password.
     * 6. If successful, generates and returns a new JWT token for the user.
     *
     * @param userId The unique identifier of the user whose password is being changed.
     * @param changePassword The request object containing:
     *        - [oldPassword]: the current password to verify;
     *        - [newPassword]: the new password to set.
     *
     * @return Either an error message (as [String]) in case of:
     *         - user not found (invalid or missing [userId]);
     *         - incorrect old password;
     *         - new password identical to old password;
     *         - failure to hash the new password;
     *         - database update failure or other technical error;
     *         or a JWT token string (indicating successful password change and authentication).
     *
     * Note:
     * - The function does NOT throw exceptions — all errors are wrapped in [Either.Left].
     * - Password verification and hashing use BCrypt for security.
     * - A new JWT token is returned on success, effectively re-authenticating the user after password change.
     * - Debug print statements are included for troubleshooting (should be replaced with proper logging in production).
     */
    override suspend fun changePassword(
        userId: String,
        changePassword: ChangePasswordRequest
    ): Either<String, String> {
        val userResult = service.findUser(userId = userId)
        val user = userResult.getOrNull() ?: return Either.Left(AppConstants.ErrorMessage.MESSAGE9)
        val isOldPasswordValid = try {
            BCrypt.verifyer().verify(changePassword.oldPassword.toCharArray(), user.password).verified
        } catch (e: Exception) {
            println("BCrypt verification error for user $userId: ${e.message}")
            false
        }

        if (!isOldPasswordValid) {
            return Either.Left(AppConstants.ErrorMessage.MESSAGE11)
        }

        if (changePassword.oldPassword == changePassword.newPassword) {
            return Either.Left(AppConstants.ErrorMessage.MESSAGE12)
        }

        val hashedNewPassword = BCrypt.withDefaults().hashToString(12, changePassword.newPassword.toCharArray())

        println("DEBUG: BCrypt: ${hashedNewPassword}")
        val updateResult = service.update(user.copy(password = hashedNewPassword))

        return when (updateResult) {
            is Either.Right -> {
                val updatedUser = updateResult.value
                Either.Right(JwtController.tokenProvider(JwtTokenRequest(updatedUser.userId, updatedUser.email)))
            }

            is Either.Left -> {
                Either.Left(updateResult.value.message ?: AppConstants.ErrorMessage.MESSAGE13)
            }
        }
    }

    /**
     * Generates and sends a one-time password (OTP) for password reset to the specified email.
     *
     * The function performs the following steps:
     * - Looks up the user by the provided email.
     * - If the user does not exist, returns an error.
     * - Otherwise, generates an OTP, saves it to the user record, and returns the code.
     *
     * @param forgetPasswordRequest Data transfer object with the user's email required to initiate the reset.
     * @return [Either<String, String>] where:
     *   - [Left] contains an error message (e.g., "User not found" or "Forget Password failed").
     *   - [Right] contains the successfully generated OTP code sent to the user.
     */
    override suspend fun forgetPassword(forgetPasswordRequest: ForgetPasswordRequest): Either<String, String> =
        when (val userResult = service.findUserEmail(forgetPasswordRequest.email)) {
            is Either.Left -> Either.Left(AppConstants.ErrorMessage.MESSAGE9)
            is Either.Right -> {
                val otp = generateOTP()
                val updateResult = service.update(userResult.value.copy(otpCode = otp))
                if (updateResult.isRight()) {
                    Either.Right(otp)
                } else {
                    Either.Left(AppConstants.ErrorMessage.MESSAGE14)
                }

            }
        }

    /**
     * Processes a password reset request by validating the provided OTP and updating the password.
     *
     * The function performs the following checks:
     * - Verifies the user exists (returns error otherwise).
     * - Matches the provided verification code with the stored OTP.
     * - Ensures the new password differs from the current one (hashed).
     * - Updates the password and clears the OTP on success.
     *
     * @param resetPasswordRequest [ResetRequest] with user's email, OTP code, and desired new password.
     * @return [Either<String, Int>] where:
     *   - [Left] contains an error message (e.g., "User not found" or "New password cannot be the same as current password").
     *   - [Right] returns [AppConstants.DataBaseTransaction.FOUND] on successful update or [AppConstants.DataBaseTransaction.NOT_FOUND] if verification fails.
     */
    override suspend fun resetPassword(resetPasswordRequest: ResetRequest): Either<String, Int> =
        when (val userResult = service.findUserEmail(resetPasswordRequest.email)) {
            is Either.Left -> Either.Left(AppConstants.ErrorMessage.MESSAGE9)
            is Either.Right -> {
                if (userResult.value.otpCode == resetPasswordRequest.verificationCode) {
                    if (BCrypt.verifyer()
                            .verify(resetPasswordRequest.newPassword.toCharArray(), userResult.value.password).verified
                    ) {
                        Either.Left(AppConstants.ErrorMessage.MESSAGE15)
                    } else {
                        val updateUser = userResult.value.copy(
                            password = BCrypt.withDefaults()
                                .hashToString(12, resetPasswordRequest.newPassword.toCharArray())
                        )
                        service.update(updateUser)
                        Either.Right(AppConstants.DataBaseTransaction.FOUND)
                    }

                } else {
                    Either.Right(AppConstants.DataBaseTransaction.NOT_FOUND)
                }
            }
        }
}