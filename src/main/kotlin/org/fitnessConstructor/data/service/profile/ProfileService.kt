package org.fitnessConstructor.data.service.profile

import arrow.core.Either
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.fitnessConstructor.data.entities.profile.ProfileDocument
import org.fitnessConstructor.utils.AppConstants
import org.fitnessConstructor.utils.CommonException
import org.fitnessConstructor.utils.UserNotExistException

interface ProfileService {
    suspend fun create(profileDocument: ProfileDocument): Either<CommonException, ProfileDocument>
    suspend fun update(profileDocument: ProfileDocument): Either<CommonException, ProfileDocument>
    suspend fun findAll(): Either<Exception, List<ProfileDocument>>
    suspend fun findProfile(profileId: String): Either<Exception, ProfileDocument>
    suspend fun findProfileEmail(email: String): Either<Exception, ProfileDocument>
    suspend fun delete(profileId: String): Either<Exception, Unit>
}

class ProfileServiceImpl(dataBase: MongoDatabase) : ProfileService {
    private val profileCollection =
        dataBase.getCollection<ProfileDocument>(AppConstants.DataBaseCollections.PROFILE_COLLECTIONS)

    override suspend fun create(profileDocument: ProfileDocument): Either<CommonException, ProfileDocument> {

        val filter = Filters.eq("email", profileDocument.email)
        val existingProfile = profileCollection.find(filter).firstOrNull()
        if (existingProfile != null) {
            return Either.Left(CommonException(AppConstants.ErrorMessage.MESSAGE1))
        }
        val result = profileCollection.insertOne(profileDocument)
        return if (result.wasAcknowledged()) {
            Either.Right(profileDocument)
        } else {
            Either.Left(CommonException(AppConstants.ErrorMessage.MESSAGE2))
        }
    }

    override suspend fun update(profileDocument: ProfileDocument): Either<CommonException, ProfileDocument> {
        val filter = Filters.eq("_id", profileDocument.profileId)
        val result = profileCollection.replaceOne(filter = filter, replacement = profileDocument)

        return if (result.wasAcknowledged()) {
            Either.Right(profileDocument)
        } else {
            Either.Left(CommonException(AppConstants.ErrorMessage.MESSAGE3))
        }
    }

    override suspend fun findAll(): Either<Exception, List<ProfileDocument>> {
        val result = profileCollection.find().toList()
        return if (result.isNotEmpty()) {
            Either.Right(result)
        } else {
            Either.Left(UserNotExistException())
        }
    }

    override suspend fun findProfile(profileId: String): Either<Exception, ProfileDocument> {
        val filter = Filters.eq("_id", profileId)
        val user = profileCollection.find(filter).firstOrNull()
        return if (user != null) {
            Either.Right(user)
        } else Either.Left(UserNotExistException())
    }

    override suspend fun findProfileEmail(email: String): Either<Exception, ProfileDocument> {
        val normalizedEmail = email.trim().lowercase()
        val filter = Filters.eq("email", normalizedEmail)
        val user = profileCollection.find(filter).firstOrNull()
        return if (user != null) {
            Either.Right(user)
        } else Either.Left(UserNotExistException())
    }

    override suspend fun delete(profileId: String): Either<Exception, Unit> {
        val filter = Filters.eq("_id", profileId)
        val result = profileCollection.deleteOne(filter)
        return if (result.wasAcknowledged()) {
            Either.Right(Unit)
        } else {
            Either.Left(CommonException(AppConstants.ErrorMessage.MESSAGE4))
        }
    }
}