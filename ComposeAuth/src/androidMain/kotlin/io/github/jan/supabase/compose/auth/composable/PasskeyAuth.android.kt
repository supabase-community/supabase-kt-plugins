package io.github.jan.supabase.compose.auth.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.passkey.PasskeyRegistrationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun ComposeAuth.rememberPasskeyRegistration(
    onResult: (PasskeyRegistrationResult) -> Unit
): PasskeyRegistrationState {
    val state = remember { PasskeyRegistrationState() }
    val context = LocalContext.current
    LaunchedEffect(state.started) {
        if(state.started) {
            withContext(Dispatchers.IO) { // TODO: maybe custom
                val options = supabaseClient.auth.passkeys.startRegistration()
                val cm = CredentialManager.create(context)
                val request = CreatePublicKeyCredentialRequest( // TODO: check API level
                    requestJson =  options.options.toString(),
                    preferImmediatelyAvailableCredentials = false // TODO: custom
                )
                try {
                    val response = cm.createCredential(context, request)
                    if (response.type == PublicKeyCredential.TYPE_PUBLIC_KEY_CREDENTIAL && response is CreatePublicKeyCredentialResponse) {
                        val metadata = supabaseClient.auth.passkeys.verifyRegistration(
                            options.challengeId,
                            response.registrationResponseJson
                        )
                        onResult(PasskeyRegistrationResult.Success(metadata))
                    } else {
                        onResult(PasskeyRegistrationResult.Error("Invalid credential"))
                    }
                } catch(e: CreateCredentialCancellationException) {
                    onResult(PasskeyRegistrationResult.CancelledByUser)
                } catch(e: CreateCredentialException) { // TODO: maybe handle retryable error
                    onResult(PasskeyRegistrationResult.Error(e.message ?: "Credential exception", e))
                }

            }
        }
    }
    return state
}