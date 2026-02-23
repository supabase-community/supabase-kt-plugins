package io.github.jan.supabase.compose.auth.composable

import platform.AuthenticationServices.ASAuthorizationAppleIDCredential

/**
 * Represents thee result of a successful sign in
 * @see NativeSignInResult.Success
 */
actual sealed interface SignInResultData {
    /**
     * The data of a Native Google Sign in
     */
    actual class Google : SignInResultData
    /**
     * The data of a Native Apple Sign in
     * @param credential The received credential
     */
    actual class Apple(val credential: ASAuthorizationAppleIDCredential) : SignInResultData
}