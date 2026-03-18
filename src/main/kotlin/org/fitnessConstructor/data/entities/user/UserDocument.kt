package am.sig_2.api.data.entities.user

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class UserDocument(
    @BsonId
    val userId: String = ObjectId().toString(),
    val username: String,
    val email: String,
    val password: String,
    val otpCode: String
) {
}