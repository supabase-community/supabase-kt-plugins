package io.github.jan.supabase.compose.auth

import io.github.jan.supabase.auth.providers.IDTokenProvider
import kotlinx.serialization.json.JsonObject

/**
 * Callback for when an id token is received from a native sign in provider
 */
fun interface IdTokenCallback {

    /**
     * Invokes the callback
     * @param composeAuth The [ComposeAuth] instance
     * @param idToken The id token received
     * @param provider The native auth provider
     * @param nonce an optional nonce
     * @param extraData Extra data to pass to the sign-in
     */
    suspend operator fun invoke(composeAuth: ComposeAuth, idToken: String, provider: IDTokenProvider, nonce: String?, extraData: JsonObject?)

}