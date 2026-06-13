package com.mgacreative.mgaglobal.core.domain.showroom

import com.mgacreative.mgaglobal.core.network.SupabaseConfig
import com.mgacreative.mgaglobal.getNowMillis
import com.mgacreative.mgaglobal.core.auth.SessionManager
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.mgacreative.mgaglobal.core.util.IntToBooleanSerializer

@Serializable
data class ShowroomProduct(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: String = "",
    val description: String = "",
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("owner_id")
    val ownerId: String = "",
    @SerialName("company_name")
    val companyName: String = "",
    val country: String = "",
    @SerialName("is_premium")
    @Serializable(with = IntToBooleanSerializer::class)
    val isPremium: Boolean = false,
    @SerialName("created_at")
    val createdAt: Long = 0L
)

class ProductService {

    suspend fun saveProduct(product: ShowroomProduct): Result<Unit> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val userId = SessionManager.getUserId()
            if (userId == "guest") return Result.failure(Exception("Oturum açılmamış"))
            
            var finalImageUrl = product.imageUrl
            
            if (product.imageUrl != null && product.imageUrl!!.startsWith("data:image/")) {
                val base64Header = product.imageUrl!!.substringBefore(",", "")
                val extension = if (base64Header.contains("image/png")) "png" 
                               else if (base64Header.contains("image/jpeg")) "jpg"
                               else "webp"

                val base64Data = product.imageUrl!!.substringAfter("base64,")
                @OptIn(ExperimentalEncodingApi::class)
                val imageBytes = Base64.Default.decode(base64Data)
                
                val filename = "prod_${getNowMillis()}.$extension"
                val bucket = client.storage.from("products")
                bucket.upload(filename, imageBytes)
                
                finalImageUrl = bucket.publicUrl(filename)
            }

            val docId = product.id.ifEmpty { "prod_${getNowMillis()}" }
            
            val newProduct = product.copy(
                id = docId,
                ownerId = userId,
                imageUrl = finalImageUrl,
                createdAt = if (product.createdAt == 0L) getNowMillis() else product.createdAt
            )

            client.postgrest.from("products").upsert(newProduct)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllProducts(): Result<List<ShowroomProduct>> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val list = client.postgrest.from("products")
                .select()
                .decodeList<ShowroomProduct>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByOwnerId(ownerId: String): Result<List<ShowroomProduct>> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val list = client.postgrest.from("products")
                .select {
                    filter {
                        eq("owner_id", ownerId)
                    }
                }.decodeList<ShowroomProduct>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(productId: String): Result<ShowroomProduct> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val product = client.postgrest.from("products")
                .select {
                    filter {
                        eq("id", productId)
                    }
                }.decodeSingle<ShowroomProduct>()
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            client.postgrest.from("products").delete {
                filter {
                    eq("id", productId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOwnProducts(): Result<List<ShowroomProduct>> {
        val userId = SessionManager.getUserId()
        if (userId == "guest") return Result.failure(Exception("Oturum açılmamış"))
        return getProductsByOwnerId(userId)
    }

    suspend fun getProductsByCategory(category: String): Result<List<ShowroomProduct>> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val list = client.postgrest.from("products")
                .select {
                    filter {
                        eq("category", category)
                    }
                }.decodeList<ShowroomProduct>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
