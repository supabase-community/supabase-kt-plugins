package io.github.jan.supabase.compose.auth.composable

actual sealed interface SignInResultData {
    actual class Google : SignInResultData
    actual class Apple : SignInResultData
}