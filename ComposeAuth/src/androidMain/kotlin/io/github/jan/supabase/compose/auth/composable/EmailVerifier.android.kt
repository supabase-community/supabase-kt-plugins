package io.github.jan.supabase.compose.auth.composable

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.DigitalCredential
import androidx.credentials.ExperimentalDigitalCredentialApi
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetDigitalCredentialOption
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.SdJwtParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.json.JSONObject

actual class EmailVerifier {
    private lateinit var activity: Activity
    private lateinit var scope: CoroutineScope
    private lateinit var onResult: (Result<VerifiedUserInfo>) -> Unit

    constructor(
        activity: Activity,
        scope: CoroutineScope,
        onResult: (Result<VerifiedUserInfo>) -> Unit
    ) {
        this.activity = activity
        this.scope = scope
        this.onResult = onResult
    }

    @OptIn(ExperimentalDigitalCredentialApi::class)
    actual fun verify(nonce: String?) {
        scope.launch {
            try {
                val credentialManager = CredentialManager.create(activity)
                val openId4vpRequest = buildOpenId4vpRequest(nonce)

                val getDigitalCredentialOption =
                    GetDigitalCredentialOption(requestJson = openId4vpRequest)
                val request = GetCredentialRequest(listOf(getDigitalCredentialOption))
                val result = credentialManager.getCredential(activity, request)
                when (val credential = result.credential) {
                    is DigitalCredential -> {
                        val responseJsonString = credential.credentialJson
                        val responseData = JSONObject(responseJsonString)
                        val vpToken = responseData.getJSONObject("vp_token")

                        val credentialId = vpToken.keys().next()
                        val rawSdJwt = vpToken.getJSONArray(credentialId).getString(0)

                        val claims = SdJwtParser.parse(rawSdJwt)
                        val email = claims["email"]?.jsonPrimitive?.content
                        onResult(
                            if (email?.isNotEmpty() == true)
                                Result.failure(Exception("Email claim is missing or empty in SD-JWT response"))
                            else
                                Result.success(VerifiedUserInfo(token = rawSdJwt))
                        )
                    }

                    else -> {
                        onResult(Result.failure(Exception("Unexpected credential type returned: ${credential.javaClass.name}")))
                    }
                }
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    private fun buildOpenId4vpRequest(nonce: String?): String {
        val json = buildJsonObject {
            putJsonArray("requests") {
                addJsonObject {
                    put("protocol", "openid4vp-v1-unsigned")
                    putJsonObject("data") {
                        put("response_type", "vp_token")
                        put("response_mode", "dc_api")
                        nonce?.let { put("nonce", it) }
                        putJsonObject("dcql_query") {
                            putJsonArray("credentials") {
                                addJsonObject {
                                    put("id", "user_info_query")
                                    put("format", "dc+sd-jwt")
                                    putJsonObject("meta") {
                                        putJsonArray("vct_values") {
                                            add("UserInfoCredential")
                                        }
                                    }
                                    putJsonArray("claims") {
                                        addJsonObject {
                                            putJsonArray("path") {
                                                add("email")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return json.toString()
    }
}

@Composable
actual fun ComposeAuth.rememberEmailVerifier(
    onResult: (Result<VerifiedUserInfo>) -> Unit
): EmailVerifier {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activity = remember(context) {
        context.findActivity() ?: error("EmailVerifier requires an Activity context")
    }
    return remember(activity, scope) {
        EmailVerifier(activity, scope, onResult)
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
