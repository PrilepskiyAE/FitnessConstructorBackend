package org.fitnessConstructor.plugins

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase

fun configureMongoDatabase(): MongoDatabase {
    val mongoHost = System.getenv("MONGODB_HOST") ?: "localhost"
    val mongoPort = System.getenv("MONGODB_PORT")?.toInt() ?: 27017
    val databaseName = System.getenv("MONGODB_DATABASE") ?: "application_db"
    val username = System.getenv("MONGODB_USERNAME") ?: "my_admin"
    val password = System.getenv("MONGODB_PASSWORD") ?: "StrongPassword456!"
    val connectionString = "mongodb://$username:$password@$mongoHost:$mongoPort/$databaseName?authSource=admin"
    val client = MongoClient.create(connectionString = connectionString)
    return  client.getDatabase(databaseName = databaseName)

}