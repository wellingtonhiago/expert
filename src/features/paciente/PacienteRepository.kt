package ai.features.paciente

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId

/**
 * Port: data-access contract for [Paciente].
 * Implementations live in the same feature package (adapters).
 */
interface PacienteRepository {
    suspend fun insert(paciente: Paciente): Boolean
    suspend fun findAll(): List<Paciente>
    suspend fun findById(id: String): Paciente?
    suspend fun findByName(nome: String): List<Paciente>
    suspend fun replace(id: String, paciente: Paciente): Paciente?
    suspend fun deleteById(id: String): Paciente?
}

/**
 * MongoDB adapter implementing [PacienteRepository].
 */
class PacienteMongoRepository(database: MongoDatabase) : PacienteRepository {
    private val collection = database.getCollection<Paciente>("pacientes")

    override suspend fun insert(paciente: Paciente): Boolean =
        collection.insertOne(paciente).wasAcknowledged()

    override suspend fun findAll(): List<Paciente> =
        collection.find().toList()

    override suspend fun findById(id: String): Paciente? =
        collection.find(Filters.eq("_id", ObjectId(id))).firstOrNull()

    override suspend fun findByName(nome: String): List<Paciente> =
        collection.find(Filters.eq("nome", nome)).toList()

    override suspend fun replace(id: String, paciente: Paciente): Paciente? =
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), paciente)

    override suspend fun deleteById(id: String): Paciente? =
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
}
