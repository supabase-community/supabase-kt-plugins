package io.github.jan.supabase.compose.auth.composable.passkey

import io.github.jan.supabase.auth.passkey.PasskeyMetadata

sealed interface PasskeyRegistrationResult {

    data class Success(val metadata: PasskeyMetadata): PasskeyRegistrationResult

    data object CancelledByUser : PasskeyRegistrationResult

    data class Error(val message: String, val exception: Exception? = null): PasskeyRegistrationResult

}