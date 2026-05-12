package io.github.jan.supabase.compose.auth.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.passkey.PasskeyRegistrationResult

class PasskeyRegistrationState {

    internal var started by mutableStateOf(false)

    fun startRegistration() {
        started = true
    }

}

@Composable
expect fun ComposeAuth.rememberPasskeyRegistration(onResult: (PasskeyRegistrationResult) -> Unit): PasskeyRegistrationState

@Composable
expect fun ComposeAuth.rememberPasskeySignIn()