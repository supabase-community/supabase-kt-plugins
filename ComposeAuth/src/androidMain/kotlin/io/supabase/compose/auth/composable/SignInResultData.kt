@file:Suppress("UndocumentedPublicClass", "UndocumentedPublicFunction", "UndocumentedPublicProperty")
package io.supabase.compose.auth.composable

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

actual sealed interface SignInResultData {
    actual class Google(val credential: GoogleIdTokenCredential) : SignInResultData
    actual class Apple : SignInResultData
}