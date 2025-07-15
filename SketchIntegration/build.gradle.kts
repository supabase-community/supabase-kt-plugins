plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.android.library.get().pluginId)
}

description = "Extends supabase-kt with a Sketch integration for easy image loading"
version = supabaseVersion

repositories {
    mavenCentral()
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    defaultConfig()
    composeTargets()
    jvmToolchain(8)
    sourceSets {
        commonMain {
            dependencies {
                addModules(SupabaseModule.STORAGE)
                api(libs.sketch.http)
            }
        }
        commonTest {
            dependencies {
                implementation(project(":test-common"))
                implementation(libs.bundles.testing)
            }
        }
    }
}

configureLibraryAndroidTarget()