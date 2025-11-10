# Supabase-kt Compose Auth

Extends gotrue-kt with Native Auth composables for Compose Multiplatform

Supported targets:

| Target | **JVM** | **Android** | **JS** | **Wasm** | **iOS** |
|--------|---------|-------------|--------|----------|---------|
| Status | ✅       | ✅           | ✅      | ✅        | ✅       |

> Native Google Auth is only supported on Android and iOS* and Native Apple Auth is only supported on iOS. Other targets or combinations rely on `auth-kt` for OAuth. \
> \* starting with version `3.3.0`

<details>

<summary>In-depth Kotlin targets</summary>

**JS**: Browser

**Wasm**: wasm-js

**iOS**: iosArm64, iosSimulatorArm64, iosX64

</details>

# Installation

Newest version: [![](https://img.shields.io/github/release/supabase-community/supabase-kt?label=)](https://github.com/supabase-community/supabase-kt/releases)

```kotlin
dependencies {
    implementation("io.github.jan-tennert.supabase:compose-auth:VERSION")
}
```

Install the plugin in your SupabaseClient. See the [documentation](https://supabase.com/docs/reference/kotlin/initializing) for more information

```kotlin
val supabase = createSupabaseClient(
    supabaseUrl = "https://id.supabase.co",
    supabaseKey = "apikey"
) {
    //...
    install(Auth) {
        //your config
    }
    install(ComposeAuth) {
        googleNativeLogin(serverClientId = "google-client-id")
        appleNativeLogin()
    }
}
```

# Native Auth Support

Currently, Compose Auth only supports Native Auth for
Android with Google (via the Credential Manager) and iOS with Apple, other variations such as JS and JVM rely on fallback which
by default is GoTrue-kt OAuth flow.

To learn how you can use this plugin in your compose project, visit [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#readme)

# Usage

The composable can be accessed trough `composeAuth` property from `supabase`

```kotlin
val action = supabase.composeAuth.rememberSignInWithGoogle(
    onResult = { result -> //optional error handling
        when (result) {
            is NativeSignInResult.Success -> {}
            is NativeSignInResult.ClosedByUser -> {}
            is NativeSignInResult.Error -> {}
            is NativeSignInResult.NetworkError -> {}
        }
    },
    onIdToken = ComposeAuth.LINK_IDENTITY_CALLBACK, // optional: if you want to link an identity to an existing account rather than signing in
    fallback = { // optional: override custom fallback handling, not required by default

    }
)

Button(
    onClick = { action.startFlow() } //optional: you can also pass in extra data for the user like a name. A nonce is automatically generated, but you can also pass in a custom nonce 
) {
    Text("Google Login")
}
```

You can also provide a custom `onIdToken` value, if you want to handle the signing-in yourself.

# Native Google Auth on Android

Here is a small guide on how to use Native Google Auth on Android:

1. Create a project in your [Google Cloud Developer Console](https://console.cloud.google.com/)
2. Create OAuth credentials for a Web application, and use your Supabase callback url as redirect url. (**https://ID.supabase.co/auth/v1/callback**)
3. Put in the Web OAuth in your Supabase Auth Settings for Google in the Dashboard
4. Create OAuth credentials for an Android app, and put in your package name and SHA-1 certificate (which you can get by using `gradlew signingReport`)
5. Put the Android OAuth client id to the authorized client ids in the Supabase Dashboard
6. Use the **Web** OAuth client id in the Compose Auth plugin

# Native Google Auth on iOS
Before start, make sure your iOS app works well first, it would be easier to isolate the issue from this step to resolve
1. Create a project in your [Google Cloud Developer Console](https://console.cloud.google.com/)
2. Create OAuth credentials for a Web application, iOS version
3. Put in Client ID of Web OAuth, Apple OAuth in your Supabase Auth Settings for Google in the Dashboard
4. Set up XCode 26
   Add Client ID and Reversed Client ID (Retrieved from iOS OAuth details on Google Console). Your `Info.plist` will look like this:
```swift
<dict>
	<key>CFBundleIdentifier</key>
	<string>io.github.jan.supabase.ios</string>
	<key>GIDClientID</key>
	<string>YOUR_CLIENT_ID</string>
	<key>CADisableMinimumFrameDurationOnPhone</key>
	<true/>
	<key>CFBundleURLTypes</key>
	<array>
		<dict>
			<key>CFBundleTypeRole</key>
			<string>Editor</string>
			<key>CFBundleURLSchemes</key>
			<array>
				<string>YOUR_REVERSED_CLIENT_ID</string>
			</array>
		</dict>
	</array>
</dict>
```

5. Download `exportedNativeBridge` at `/ComposeAuth/exportedNativeBridge` in this repository
   In XCode, add it as dependency
   Step 1: Right click on the left tool bar > Select Add dependencies
   <img width="750" height="509" alt="Screenshot 2025-11-01 at 00 38 41" src="https://github.com/user-attachments/assets/3b0f1b05-8946-43bf-b46b-e21ed70b811b" />
   
   Step 2: From opened dialog > Add local
   <img width="750" height="618" alt="Screenshot 2025-10-30 at 21 10 28" src="https://github.com/user-attachments/assets/fa35f129-a3b1-4403-9e92-682738f1ada6" />
   
   Step 3: Continue Add package
   
   <img width="750" height="632" alt="Screenshot 2025-11-01 at 00 38 51" src="https://github.com/user-attachments/assets/92b709fa-57d2-41b5-b066-e391f132231c" />

   Step 4: Add GoogleSignIn 9.0.0 or the one compatible with your project as below:
   <img width="750" height="452" alt="Screenshot 2025-11-02 at 20 51 45" src="https://github.com/user-attachments/assets/7bdcd805-c3a6-4500-ace9-f5a389cdabdf" />

7. Build your app. It should work now.

