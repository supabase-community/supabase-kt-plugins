plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.android.library.get().pluginId)
}

description = "Extends supabase-kt with a Apollo GraphQL Client"
version = supabaseVersion

repositories {
    mavenCentral()
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    defaultConfig()
    jvmTargets()
    jsTarget()
    iosTargets()
    macosTargets()
    watchosArm64()
    watchosSimulatorArm64()
    wasmJsTarget()
    tvosTargets()
    sourceSets {
        val commonMain by getting {
            dependencies {
                addModules(SupabaseModule.AUTH, SupabaseModule.SUPABASE)
                api(libs.apollo.kotlin)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.bundles.testing)
                implementation(project(":test-common"))
            }
        }
    }
}

configureLibraryAndroidTarget()