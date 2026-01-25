package io.github.jan.supabase.compose.auth.composable

/**
 * Represents the response of a Native SignIn
 */
sealed interface NativeSignInResult {

    /**
     * User successfully signed in
     */
    data class Success(val data: SignInResultData) : NativeSignInResult

    /**
     * User closed the login dialog
     */
    data object ClosedByUser : NativeSignInResult

    /**
     * Network error occurred
     * @property message The error message
     */
    data class NetworkError(val message: String) : NativeSignInResult

    /**
     * Error occurred
     * @property message The error message
     * @property exception The exception that caused the error
     */
    data class Error(val message: String, val exception: Exception? = null) : NativeSignInResult
}

expect sealed interface SignInResultData {
    class Google : SignInResultData
    class Apple : SignInResultData
}

val SignInResultData.apple get() = this as SignInResultData.Apple

val SignInResultData.google get() = this as SignInResultData.Google
