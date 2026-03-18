package org.fitnessConstructor.di

import org.fitnessConstructor.modules.profile.controller.ProfileController
import org.fitnessConstructor.modules.profile.repository.ProfileRepo
import org.fitnessConstructor.plugins.configureMongoDatabase
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.fitnessConstructor.data.service.profile.ProfileService
import org.fitnessConstructor.data.service.profile.ProfileServiceImpl
import org.fitnessConstructor.data.service.user.UserService
import org.fitnessConstructor.data.service.user.UserServiceImpl
import org.fitnessConstructor.modules.auth.controller.AuthController
import org.fitnessConstructor.modules.auth.repository.AuthRepo
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val controllerModule = module {
    singleOf(::AuthController) {
        bind<AuthRepo>()
    }
    singleOf(::ProfileController) {
        bind<ProfileRepo>()
    }

}

val databaseModule = module {
    single<MongoDatabase> { configureMongoDatabase() }
}

val serviceModule = module {
    singleOf(::UserServiceImpl) {
        bind<UserService>()
    }
    singleOf(::ProfileServiceImpl){
        bind<ProfileService>()
    }

}