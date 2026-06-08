package io.github.jan.supabase.compose.auth.composable

import androidx.compose.runtime.Composable
import io.github.jan.supabase.compose.auth.ComposeAuth

actual class EmailVerifier {
    actual fun verify(nonce: String?) {
    }
}

@Composable
actual fun ComposeAuth.rememberEmailVerifier(onResult: (Result<VerifiedUserInfo>) -> Unit): EmailVerifier {
    TODO("Not yet implemented")
}