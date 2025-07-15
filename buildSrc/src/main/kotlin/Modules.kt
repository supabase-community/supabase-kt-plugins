import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

enum class SupabaseModule(val module: String, val extern: Boolean = false) {
    SUPABASE("io.github.jan-tennert.supabase:supabase-kt", true),
    AUTH("io.github.jan-tennert.supabase:auth-kt", true),
    STORAGE("io.github.jan-tennert.supabase:storage-kt", true),
    REALTIME("io.github.jan-tennert.supabase:realtime-kt", true),
    FUNCTIONS("io.github.jan-tennert.supabase:functions-kt", true),
    POSTGREST("io.github.jan-tennert.supabase:postgrest-kt", true),
    COMPOSE_AUTH("compose-auth"),
    COMPOSE_AUTH_UI("compose-auth-ui"),
}

fun KotlinDependencyHandler.addModules(vararg modules: SupabaseModule) {
    modules.forEach {
        if(it.extern) {
            api(it.module + ":" + project.supabaseVersion)
        } else {
            api(project(":${it.module}"))
        }
    }
}

fun DesktopExtension.configureComposeDesktop(
    name: String,
) {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = name
            packageVersion = "1.0.0"
        }
    }
}