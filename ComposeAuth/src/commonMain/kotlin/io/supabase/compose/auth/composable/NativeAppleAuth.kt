package io.supabase.compose.auth.composable

import androidx.compose.runtime.Composable
import io.supabase.auth.providers.Apple
import io.supabase.compose.auth.ComposeAuth
import io.supabase.compose.auth.IdTokenCallback
import io.supabase.compose.auth.fallbackLogin

/**
 * Composable function that implements Native Apple Auth.
 *
 * On unsupported platforms it will use the [fallback]
 *
 * @param onResult Callback for the result of the login
 * @param fallback Fallback function for unsupported platforms
 * @return [NativeSignInState]
 */
@Composable
expect fun ComposeAuth.rememberSignInWithApple(
    onResult: (NativeSignInResult) -> Unit,
    onIdToken: IdTokenCallback = ComposeAuth.SIGN_IN_CALLBACK,
    fallback: suspend () -> Unit = { fallbackLogin(Apple) }
) : NativeSignInState