package ai.infrastructure

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.config.tryGetString
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Creates the [MongoDatabase] used by the application.
 * Reads credentials and database name from `application.yaml` / environment variables:
 *  - `DB_MONGO_USER`
 *  - `DB_MONGO_PASSWORD`
 *  - `DB_MONGO_DATABASE_NAME`
 *
 * The underlying [MongoClient] is closed automatically when the application stops.
 */
fun Application.connectToMongoDB(): MongoDatabase {
    val user = environment.config.tryGetString("db.mongo.user")
    val password = environment.config.tryGetString("db.mongo.password")
    val databaseName = environment.config.tryGetString("db.mongo.database.name")

    require(!user.isNullOrBlank()) { "MongoDB user is not configured (db.mongo.user). Set DB_MONGO_USER." }
    require(!password.isNullOrBlank()) { "MongoDB password is not configured (db.mongo.password). Set DB_MONGO_PASSWORD." }
    require(!databaseName.isNullOrBlank()) { "MongoDB database name is not configured (db.mongo.database.name). Set DB_MONGO_DATABASE_NAME." }

    val encodedUser = URLEncoder.encode(user, StandardCharsets.UTF_8)
    val encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8)

    val uri =
        "mongodb+srv://$encodedUser:$encodedPassword@expert-agent-cluster.uu9o9xd.mongodb.net/?retryWrites=true&w=majority&appName=expert-agent-cluster"

    return runCatching {
        val mongoClient = MongoClient.create(uri)
        monitor.subscribe(ApplicationStopped) {
            mongoClient.close()
        }
        mongoClient.getDatabase(databaseName)
    }.getOrElse { ex ->
        throw IllegalStateException("Failed to initialize MongoDB client: ${ex.message}", ex)
    }
}
