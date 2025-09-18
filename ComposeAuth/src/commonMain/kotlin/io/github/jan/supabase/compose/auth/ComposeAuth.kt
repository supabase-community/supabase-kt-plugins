package io.github.jan.supabase.compose.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.SupabaseSerializer
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.IDTokenProvider
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.compose.auth.composable.NativeSignInState
import io.github.jan.supabase.logging.SupabaseLogger
import io.github.jan.supabase.logging.d
import io.github.jan.supabase.plugins.CustomSerializationConfig
import io.github.jan.supabase.plugins.CustomSerializationPlugin
import io.github.jan.supabase.plugins.SupabasePlugin
import io.github.jan.supabase.plugins.SupabasePluginProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Plugin that extends the [io.github.jan.supabase.auth.Auth] Module with composable function that enables an easy implementation of Native Auth.
 * Currently supported Google Login (Android with OneTap or CM on Android 14+) and Apple Login (iOS), other compose-supported targets rely on GoTrue login.
 *
 * To use it, install GoTrue and ComposeAuth
 * ```kotlin
 * val client = createSupabaseClient(supabaseUrl, supabaseKey) {
 *    install(GoTrue) {
 *       //your config here
 *    }
 *    install(ComposeAuth) {
 *       googleNativeLogin(/* your config parameters here */)
 *       appleNativeLogin(/* your config parameters here */)
 *    }
 * }
 * ```
 *
 * then on you screen call
 *  ```kotlin
 *  val action = auth.rememberSignInWithGoogle(
 *     onResult = {
 *        // returns NativeSignInResult
 *     },
 *     fallback = {
 *        // optional: only add fallback if you like to use custom fallback
 *     }
 * )
 *
 * Button(
 *     onClick = {
 *         action.startFlow()
 *     }
 * ) {
 *     Text(text = "Google Login")
 * }
 *  ```
 */
interface ComposeAuth : SupabasePlugin<ComposeAuth.Config>, CustomSerializationPlugin {

    /**
     * Config for [ComposeAuth]
     * @property googleLoginConfig Config for Google Login
     * @property appleLoginConfig Config for Apple Login. Currently a placeholder.
     * @property serializer The [SupabaseSerializer] to use for serialization when using [NativeSignInState.startFlow]
     */
    data class Config(
        var googleLoginConfig: GoogleLoginConfig? = null,
        var appleLoginConfig: AppleLoginConfig? = null,
        override var serializer: SupabaseSerializer? = null
    ): CustomSerializationConfig

    companion object : SupabasePluginProvider<Config, ComposeAuth> {

        override val key: String = "composeauth"

        override val logger: SupabaseLogger = SupabaseClient.createLogger("Supabase-ComposeAuth")

        /**
         * This callback can be used to sign-in/sign-up a user upon receiving an id token from native auth.
         */
        val SIGN_IN_CALLBACK = IdTokenCallback { composeAuth, idToken, provider, nonce, extra ->
            composeAuth.supabaseClient.auth.signInWith(IDToken) {
                this.provider = provider
                this.idToken = idToken
                this.nonce = nonce
                data = extra
            }
        }

        /**
         * This callback can be used to link an identity to an existing and logged-in user upon receiving an id token from native auth.
         * Can also be used to upgrade an anonymous user.
         */
        val LINK_IDENTITY_CALLBACK = IdTokenCallback { composeAuth, idToken, provider, nonce, _ ->
            composeAuth.supabaseClient.auth.linkIdentityWithIdToken(provider, idToken) {
                this.nonce = nonce
            }
        }

        override fun create(supabaseClient: SupabaseClient, config: Config): ComposeAuth {
            return ComposeAuthImpl(config, supabaseClient)
        }

        override fun createConfig(init: Config.() -> Unit): Config {
            return Config().apply(init)
        }
    }
}

/**
 * Composable plugin that handles Native Auth on supported platforms
 */
val SupabaseClient.composeAuth: ComposeAuth
    get() = pluginManager.getPlugin(ComposeAuth)

internal class ComposeAuthImpl(
    override val config: ComposeAuth.Config,
    override val supabaseClient: SupabaseClient,
) : ComposeAuth {

    private val scope = CoroutineScope(Dispatchers.Default)

    override val serializer: SupabaseSerializer = config.serializer ?: supabaseClient.defaultSerializer

    override suspend fun close() {
        scope.cancel()
    }

    override fun init() {
        if(config.googleLoginConfig?.handleSignOut != null) {
            supabaseClient.auth.sessionStatus
                .onEach {
                    if(it is SessionStatus.NotAuthenticated && it.isSignOut) {
                        ComposeAuth.logger.d { "Received sign out event from Supabase, clearing any Google credentials..." }
                        config.googleLoginConfig?.handleSignOut?.invoke()
                    }
                }
                .launchIn(scope)
        }
    }

}

internal suspend fun ComposeAuth.fallbackLogin(provider: IDTokenProvider) {
    supabaseClient.auth.signInWith(provider)
}