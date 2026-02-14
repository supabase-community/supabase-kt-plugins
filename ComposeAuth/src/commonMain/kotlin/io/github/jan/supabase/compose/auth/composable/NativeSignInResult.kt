package io.github.jan.supabase.compose.auth.composable

/**
 * Represents the response of a Native SignIn
 */
sealed interface NativeSignInResult {

    /**
     * User successfully signed in
     * @param data The data received from the Native Sign-In
     * @see SignInResultData.apple
     * @see SignInResultData.google
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

/**
 * Represents thee result of a successful sign in
 * @see NativeSignInResult.Success
 */
expect sealed interface SignInResultData {
    /**
     * The data of a Native Google Sign in
     */
    class Google : SignInResultData

    /**
     * The data of a Native Apple Sign in
     */
    class Apple : SignInResultData
}

/**
 * Casts [this] to a [SignInResultData.Apple]
 */
val SignInResultData.apple get() = this as SignInResultData.Apple

/**
 * Casts [this] to a [SignInResultData.Google]
 */
val SignInResultData.google get() = this as SignInResultData.Google
