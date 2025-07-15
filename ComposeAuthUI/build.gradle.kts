import com.android.build.gradle.internal.lint.AndroidLintAnalysisTask
import com.android.build.gradle.internal.lint.LintModelWriterTask
import com.android.build.gradle.internal.tasks.LintModelMetadataTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.compose.plugin.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

description = "Extends supabase-kt with a Apollo GraphQL Client"
version = supabaseVersion

repositories {
    mavenCentral()
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    defaultConfig()
    applyDefaultHierarchyTemplate {
        common {
            group("nonJvm") {
                withIos()
                withJs()
                withWasmJs()
            }
        }
    }
    composeTargets(JvmTarget.JVM_11)
    jvmToolchain(11)
    sourceSets {
        commonMain {
            dependencies {
                api(compose.ui)
                addModules(SupabaseModule.AUTH)
                api(compose.material3)
                api(compose.components.resources)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidsvg)
            }
        }
    }
}

configureLibraryAndroidTarget(javaVersion = JavaVersion.VERSION_11)

//see https://github.com/JetBrains/compose-multiplatform/issues/4739
tasks.withType<LintModelWriterTask> {
    dependsOn("generateResourceAccessorsForAndroidUnitTest")
}
tasks.withType<LintModelMetadataTask> {
    dependsOn("generateResourceAccessorsForAndroidUnitTest")
}
tasks.withType<AndroidLintAnalysisTask> {
    dependsOn("generateResourceAccessorsForAndroidUnitTest")
}

compose {
    resources {
        packageOfResClass = "io.github.jan.supabase.compose.auth.ui.resources"
    }
}