package ai.features.nutricionista

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId

interface NutricionistaRepository {
    suspend fun insert(nutricionista: Nutricionista)
    suspend fun findAll(): List<Nutricionista>
    suspend fun findById(id: String): Nutricionista?
    suspend fun findByName(nome: String): List<Nutricionista>
    suspend fun replace(id: String, nutricionista: Nutricionista): Nutricionista?
    suspend fun deleteById(id: String): Nutricionista?
}

class NutricionistaMongoRepository(database: MongoDatabase) : NutricionistaRepository {
    private val collection: MongoCollection<Nutricionista> =
        database.getCollection<Nutricionista>("nutricionistas")

    override suspend fun insert(nutricionista: Nutricionista) {
        collection.insertOne(nutricionista)
    }

    override suspend fun findAll(): List<Nutricionista> =
        collection.find().toList()

    override suspend fun findById(id: String): Nutricionista? =
        collection.find(Filters.eq("_id", ObjectId(id))).firstOrNull()

    override suspend fun findByName(nome: String): List<Nutricionista> =
        collection.find(Filters.eq("nome", nome)).toList()

    override suspend fun replace(id: String, nutricionista: Nutricionista): Nutricionista? =
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), nutricionista)

    override suspend fun deleteById(id: String): Nutricionista? =
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
}
