package org.fitnessConstructor.data.entities.profile

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.fitnessConstructor.data.models.response.ProfileResponse

@Serializable
data class ProfileDocument(
    @BsonId
    val profileId: String = ObjectId().toString(),
    val name: String,
    val email: String,
    val country: String,
    val host: String,
) {
    fun toResponse(): ProfileResponse = ProfileResponse(name = name, email = email, country = country, host = host)

}