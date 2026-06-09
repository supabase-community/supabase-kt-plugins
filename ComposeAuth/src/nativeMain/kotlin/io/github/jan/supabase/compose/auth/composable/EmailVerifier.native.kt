package io.github.jan.supabase.compose.auth.composable

@androidx.compose.runtime.Composable
actual fun io.github.jan.supabase.compose.auth.ComposeAuth.rememberEmailVerifier(onResult: (kotlin.Result<io.github.jan.supabase.compose.auth.composable.VerifiedUserInfo>) -> Unit): io.github.jan.supabase.compose.auth.composable.EmailVerifier {
    TODO("Not yet implemented")
}

actual class EmailVerifier {
    actual fun verify(nonce: String) {
    }
}