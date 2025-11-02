import com.android.build.gradle.internal.lint.AndroidLintAnalysisTask
import com.android.build.gradle.internal.lint.LintModelWriterTask
import com.android.build.gradle.internal.tasks.LintModelMetadataTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.compose.plugin.get().pluginId)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.spm)
}

description = "Extends gotrue-kt with Native Auth composables"
version = "3.2.7-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    defaultConfig()
    applyDefaultHierarchyTemplate {
        common {
            group("noDefault") {
                withJvm()
                withJs()
                withWasmJs()
            }
        }
    }
    jvmToolchain(11)
    composeTargets(JvmTarget.JVM_11)
    iosTargets()
    targets.forEach {
        if (it is org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget) {
            it.binaries.framework {
                baseName = "common"
                isStatic = true
            }
            it.compilations {
                val main by getting {
                    cinterops.create("nativeBridge")
                }
            }
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                addModules(SupabaseModule.AUTH)
                implementation(compose.runtime)
          //      implementation(libs.krypto)
            }
        }
        androidMain {
            dependencies {
                api(libs.android.google.id)
                api(libs.androidx.credentials)
                api(libs.androidx.credentials.play.services)
                implementation(libs.androidx.activity.compose)
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

swiftPackageConfig {
    create("nativeBridge") {
        dependency {
            linkerOpts =
                listOf("-ObjC", "-fObjC")
            remotePackageVersion(
                url = uri("https://github.com/google/GoogleSignIn-iOS.git"),
                products = {
                    add("GoogleSignIn", exportToKotlin = true)
                },
                version = "9.0.0",
            )
            exportedPackageSettings {
                includeProduct = listOf("GoogleSignIn")
            }
        }
    }
}