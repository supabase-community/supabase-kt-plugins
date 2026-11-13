package io.supabase.compose.auth.composable

import androidx.compose.runtime.Composable
import io.supabase.compose.auth.ComposeAuth
import io.supabase.compose.auth.IdTokenCallback
import io.supabase.compose.auth.defaultLoginBehavior

/**
 * Composable for Apple login with default behavior
 *
 * @param onResult Callback for the result of the login
 * @param onIdToken Callback for the id token. By default, it will be passed to the [io.supabase.auth.Auth] plugin to sign-in.
 * @param fallback Fallback function for unsupported platforms
 * @return [NativeSignInState]
 */
@Composable
actual fun ComposeAuth.rememberSignInWithApple(
    onResult: (NativeSignInResult) -> Unit,
    onIdToken: IdTokenCallback,
    fallback: suspend () -> Unit
): NativeSignInState = defaultLoginBehavior(fallback)