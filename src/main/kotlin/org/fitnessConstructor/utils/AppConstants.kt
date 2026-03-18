package org.fitnessConstructor.utils

object AppConstants {
    const val MIN = 4
    const val MAX = 30
    object SuccessMessage {
        object Password {
            const val PASSWORD_CHANGE_SUCCESS = "Password change successful"
        }

        object VerificationCode {
            const val VERIFICATION_CODE_SENT_TO = "Verification code sent to"
            const val VERIFICATION_CODE_IS_NOT_VALID = "Verification code is not valid"
        }
    }
    object ErrorMessage{
        const val MESSAGE1 = "A user with this email already exists"
        const val MESSAGE2 = "Failed to acknowledge insertion"
        const val MESSAGE3 = "Failed to acknowledge replace"
        const val MESSAGE4 = "Failed to acknowledge delete"
        const val MESSAGE5 = "A tv shows with this Key already exists"
        const val MESSAGE6 = "Failed to list empty"
        const val MESSAGE7 = "Failed  empty to key"
        const val MESSAGE9 = "User not found"
        const val MESSAGE10 = "tv show not found"
        const val MESSAGE11 = "Current password is incorrect"
        const val MESSAGE12 = "New password cannot be the same as old password"
        const val MESSAGE13 =  "Password update failed"
        const val MESSAGE14 = "Forget Password failed"
        const val MESSAGE15 = "New password cannot be the same as current password"
        const val MESSAGE16 =   "Password has been changed"
        const val MESSAGE17 =   "Error profile"

    }
    object DataBaseTransaction {
        const val FOUND = 1
        const val NOT_FOUND = 2
    }

    object DataBaseCollections{
        const val USER_COLLECTIONS = "user_collection"
        const val PROFILE_COLLECTIONS = "profile_collection"
    }

    object SmtpServer {
        const val HOST_NAME = "smtp.gmail.com"
        const val PORT = 465
        const val DEFAULT_AUTHENTICATOR = ""
        const val DEFAULT_AUTHENTICATOR_PASSWORD = ""
        const val EMAIL_SUBJECT = "Password Reset Code"
        const val SENDING_EMAIL = ""
    }

    object ImageFolder {
        const val PROFILE_IMAGE_LOCATION = "src/main/resources/profile-image/"
        const val PRODUCT_IMAGE_LOCATION = "src/main/resources/product-image/"
        const val CATEGORY_IMAGE_LOCATION = "src/main/resources/category-image/"
    }
}