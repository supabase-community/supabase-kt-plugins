package io.github.jan.supabase.compose.auth.composable

import androidx.compose.runtime.Composable
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.IdTokenCallback
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.compose.auth.hash
import io.github.jan.supabase.logging.d
import io.github.jan.supabase.logging.e
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import nativeBridge.GoogleSignInController

/**
 * Composable function that implements Native Google Auth.
 *
 * On unsupported platforms it will use the [fallback]
 *
 * @param onResult Callback for the result of the login
 * @param onIdToken Callback for the id token. By default, it will be passed to the [io.github.jan.supabase.auth.Auth] plugin to sign-in.
 * @param fallback Fallback function for unsupported platforms
 * @return [NativeSignInState]
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ComposeAuth.rememberSignInWithGoogle(
    onResult: (NativeSignInResult) -> Unit,
    onIdToken: IdTokenCallback,
    type: GoogleDialogType,
    fallback: suspend () -> Unit
): NativeSignInState {
    val state = remember { NativeSignInState(this.serializer) }
    val scope = rememberCoroutineScope()

    val googleSignInController = remember {
        GoogleSignInController()
    }

    LaunchedEffect(key1 = state.status) {
        if (state.status is NativeSignInStatus.Started) {
            val startedStatus = state.status as NativeSignInStatus.Started
            ComposeAuth.logger.d { "Starting Native Google Sign In flow on iOS" }
            try {
                if (config.googleLoginConfig != null) {
                    ComposeAuth.logger.d { "Google login config found" }
                    val hashedNonce = startedStatus.nonce?.hash()
                    ComposeAuth.logger.d { "Native Google Sign In Flow${if (hashedNonce != null) " with hashed nonce: $hashedNonce" else ""}" }
                    googleSignInController.signInCompletion(
                        completion = { idToken, errorMessage, isCancelled ->
                            scope.launch {
                                if (isCancelled) {
                                    ComposeAuth.logger.d { "Native Google Sign In Flow was closed by user" }
                                    onResult.invoke(NativeSignInResult.ClosedByUser)
                                } else if (idToken != null) {
                                    ComposeAuth.logger.d { "Id token available" }
                                    onIdToken.invoke(
                                        composeAuth = this@rememberSignInWithGoogle,
                                        result = IdTokenCallback.Result(
                                            idToken = idToken,
                                            provider = Google,
                                            nonce = startedStatus.nonce,
                                            extraData = startedStatus.extraData
                                        )
                                    )
                                    onResult.invoke(NativeSignInResult.Success())
                                } else if (errorMessage != null) {
                                    ComposeAuth.logger.d { "Error happens due to: $errorMessage" }
                                    onResult.invoke(NativeSignInResult.Error(errorMessage))
                                } else {
                                    ComposeAuth.logger.e { "Error while logging into Supabase with Google ID Token Credential" }
                                }
                            }
                        },
                        nonce = startedStatus.nonce?.hash()
                    )
                } else {
                    fallback.invoke()
                }
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                onResult.invoke(NativeSignInResult.Error(e.message ?: "error"))
            } finally {
                state.reset()
            }
        }
    }
    return state
}

@OptIn(ExperimentalForeignApi::class)
internal actual suspend fun handleGoogleSignOut() {
    GoogleSignInController.signOutGoogle()
}