package io.github.jan.supabase.compose.auth.composable

import androidx.compose.runtime.Composable
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.IdTokenCallback
import io.github.jan.supabase.compose.auth.defaultLoginBehavior

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
@Composable
actual fun ComposeAuth.rememberSignInWithGoogle(
    onResult: (NativeSignInResult) -> Unit,
    onIdToken: IdTokenCallback,
    type: GoogleDialogType,
    fallback: suspend () -> Unit
): NativeSignInState = defaultLoginBehavior(fallback)

internal actual suspend fun handleGoogleSignOut() = Unit