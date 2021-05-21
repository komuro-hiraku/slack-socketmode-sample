package me.komurohiraku.service

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * UUID Generator Interface <br />
 * https://www.uuidgenerator.net/api
 */
interface UuidGenerator {
    fun generate(): String
}

enum class UuidType {
    VERSION1, VERSION4
}

abstract class AbstractUuidGenerator: UuidGenerator {

    protected val client = OkHttpClient()

    companion object {
        fun from(type: UuidType): UuidGenerator {
            when (type) {
                UuidType.VERSION1 -> return Version1UuidGenerator()
                UuidType.VERSION4 -> return Version4UuidGenerator()
            }
        }

        const val DEFAULT_TYPE = "VERSION1"
    }
}

/**
 * UUID Version 1 Implements
 */
class Version1UuidGenerator: AbstractUuidGenerator() {

    val endpoint = "https://www.uuidgenerator.net/api/version1"

    override fun generate(): String {
        // build GET request
        val request = Request.Builder()
            .url(endpoint)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }

            return response.body!!.string()
        }
    }
}

/**
 * UUID Version 4 Implements
 */
class Version4UuidGenerator: AbstractUuidGenerator() {

    val endpoint = "https://www.uuidgenerator.net/api/version4"

    override fun generate(): String {
        val request = Request.Builder()
            .url(endpoint)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }
            return response.body!!.string()
        }
    }
}
