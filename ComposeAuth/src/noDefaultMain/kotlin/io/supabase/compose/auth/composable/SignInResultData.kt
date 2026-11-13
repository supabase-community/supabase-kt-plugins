@file:Suppress("UndocumentedPublicClass", "UndocumentedPublicFunction", "UndocumentedPublicProperty")
package io.supabase.compose.auth.composable

actual sealed interface SignInResultData {
    actual class Google : SignInResultData
    actual class Apple : SignInResultData
}