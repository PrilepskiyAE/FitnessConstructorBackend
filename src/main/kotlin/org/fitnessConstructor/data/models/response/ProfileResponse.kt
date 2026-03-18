package org.fitnessConstructor.data.models.response

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val name: String,
    val email: String,
    val country: String,
    val host: String,
)


