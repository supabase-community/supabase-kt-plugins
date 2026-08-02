package io.github.jan.supabase.compose.auth

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
internal object SdJwtParser {

    private val json = Json { ignoreUnknownKeys = true }

    fun parse(rawSdJwt: String): JsonObject {
        val claims = mutableMapOf<String, JsonElement>()

        val parts = rawSdJwt.split("~")

        if (parts.isNotEmpty()) {
            val jwtParts = parts[0].split(".")
            if (jwtParts.size >= 2) {
                try {
                    val payloadDecoded = Base64.UrlSafe.decode(jwtParts[1])
                    val payloadString = payloadDecoded.toString()
                    val payloadJson = json.parseToJsonElement(payloadString).jsonObject
                    for ((key, value) in payloadJson) {
                        if (key != "_sd" && key != "_sd_alg") {
                            claims[key] = value
                        }
                    }
                } catch (e: Exception) {
                    // Ignore payload parsing errors
                }
            }
        }

        for (i in 1 until parts.size) {
            val part = parts[i].trim()
            if (part.isEmpty()) continue

            try {
                val decoded = Base64.UrlSafe.decode(part)
                val jsonString = decoded.toString()

                if (jsonString.startsWith("[")) {
                    val array = json.parseToJsonElement(jsonString).jsonArray
                    if (array.size == 3) {
                        val key = array[1].jsonPrimitive.contentOrNull.orEmpty()
                        if (key.isNotEmpty()) {
                            claims[key] = array[2]
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore disclosure parsing errors (e.g. key binding JWT)
            }
        }

        return JsonObject(claims)
    }
}