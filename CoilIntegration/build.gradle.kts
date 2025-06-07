plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.android.library.get().pluginId)
}

description = "Extends supabase-kt with a Coil integration for easy image loading"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    defaultConfig()
    configuredAndroidTarget()
    sourceSets {
        val commonMain by getting {
            dependencies {
                addModules(SupabaseModule.STORAGE)
                api(libs.coil2)
            }
        }
    }
}

configureLibraryAndroidTarget()