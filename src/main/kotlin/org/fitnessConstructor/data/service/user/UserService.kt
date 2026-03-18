package org.fitnessConstructor.data.service.user

import am.sig_2.api.data.entities.user.UserDocument
import arrow.core.Either
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.Flow
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.fitnessConstructor.utils.AppConstants
import org.fitnessConstructor.utils.CommonException
import org.fitnessConstructor.utils.UserNotExistException
import org.fitnessConstructor.utils.emitFlow

interface UserService {
    suspend fun create(userDocument: UserDocument): Either<CommonException, UserDocument>
    suspend fun update(userDocument: UserDocument): Either<CommonException, UserDocument>
    suspend fun findAll(): Flow<List<UserDocument>>
    suspend fun findUser(userId: String): Either<Exception, UserDocument>
    suspend fun findUserEmail(email: String): Either<Exception, UserDocument>
    suspend fun delete(userId: String): Either<Exception, Unit>
}

class UserServiceImpl(dataBase: MongoDatabase) : UserService {

    private val userCollection = dataBase.getCollection<UserDocument>(AppConstants.DataBaseCollections.USER_COLLECTIONS)

    override suspend fun create(userDocument: UserDocument): Either<CommonException, UserDocument> {

        val normalizedEmail = userDocument.email.trim().lowercase()
        val userToSave = userDocument.copy(
            email = normalizedEmail,
        )
        val filter = Filters.eq("email", normalizedEmail)
        val existingUser = userCollection.find(filter).firstOrNull()
        if (existingUser != null) {
            return Either.Left(CommonException(AppConstants.ErrorMessage.MESSAGE1))
        }
        val result = userCollection.insertOne(userToSave)

        return if (result.wasAcknowledged()) {
            Either.Right(userToSave)
        } else {
            Either.Left(CommonException(AppConstants.ErrorMessage.MESSAGE2))
        }
    }

    override suspend fun update(userDocument: UserDocument): Either<CommonException, UserDocument> {
        val filter = Filters.eq("_id", userDocument.userId)
        val result = userCollection.replaceOne(filter = filter, replacement = userDocument)

        return if (result.wasAcknowledged()) {
            Either.Right(userDocument)
        } else {
            Either.Left(CommonException(AppConstants.ErrorMessage.MESSAGE3))
        }
    }

    override suspend fun findAll(): Flow<List<UserDocument>> = emitFlow {
        userCollection.find().toList()
    }

    override suspend fun findUser(userId: String): Either<Exception, UserDocument> {
        val filter = Filters.eq("_id", userId)
        val user = userCollection.find(filter).firstOrNull()
        return if (user != null) {
            Either.Right(user)
        } else Either.Left(UserNotExistException())
    }

    override suspend fun findUserEmail(email: String): Either<Exception, UserDocument> {
        val normalizedEmail = email.trim().lowercase()
        val filter = Filters.eq("email", normalizedEmail)
        val user = userCollection.find(filter).firstOrNull()
        return if (user != null) {
            Either.Right(user)
        } else Either.Left(UserNotExistException())
    }

    override suspend fun delete(userId: String): Either<Exception, Unit> {
        val filter = Filters.eq("_id", userId)
        val result = userCollection.deleteOne(filter)
        return if (result.wasAcknowledged()) {
            Either.Right(Unit)
        } else {
            Either.Left(CommonException(AppConstants.ErrorMessage.MESSAGE4))
        }
    }
}