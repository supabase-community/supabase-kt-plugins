package io.github.jan.supabase.compose.auth.composable

import platform.AuthenticationServices.ASAuthorizationAppleIDCredential

actual sealed interface SignInResultData {
    actual class Google : SignInResultData // Todo: Add Google data
    actual class Apple(val credential: ASAuthorizationAppleIDCredential) : SignInResultData
}