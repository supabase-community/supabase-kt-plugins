plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.android.library.get().pluginId)
}

description = "Extends supabase-kt with a Coil integration for easy image loading"
version = supabaseVersion

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    defaultConfig()
    configuredAndroidTarget()
    sourceSets {
        commonMain {
            dependencies {
                addModules(SupabaseModule.STORAGE)
                api(libs.coil2)
            }
        }
    }
}

configureLibraryAndroidTarget()