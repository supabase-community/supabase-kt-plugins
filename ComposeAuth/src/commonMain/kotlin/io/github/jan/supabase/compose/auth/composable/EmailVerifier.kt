package io.github.jan.supabase.compose.auth.composable

import androidx.compose.runtime.Composable
import io.github.jan.supabase.compose.auth.ComposeAuth

data class VerifiedUserInfo(
    val token: String
)

expect class EmailVerifier {
    fun verify(nonce: String?)
}

@Composable
expect fun ComposeAuth.rememberEmailVerifier(
    onResult: (Result<VerifiedUserInfo>) -> Unit
): EmailVerifier