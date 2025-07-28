plugins {
    `java-platform`
}

description = "A Kotlin Multiplatform Supabase Framework"

val bomProject = project

val excludedModules = listOf("test-common")
val supabaseModules = listOf(SupabaseModule.SUPABASE, SupabaseModule.STORAGE, SupabaseModule.AUTH, SupabaseModule.REALTIME, SupabaseModule.FUNCTIONS, SupabaseModule.POSTGREST)
val serializers = listOf("serializer-jackson", "serializer-moshi")

fun shouldIncludeInBom(candidateProject: Project) =
    excludedModules.all { !candidateProject.name.contains(it) } &&
            candidateProject.name != bomProject.name

rootProject.subprojects.filter(::shouldIncludeInBom).forEach { bomProject.evaluationDependsOn(it.path) }

dependencies {
    constraints {
        rootProject.subprojects.filter { project ->
            // Only declare dependencies on projects that will have publications
            shouldIncludeInBom(project) && project.tasks.findByName("publish")?.enabled == true
        }.forEach { api(project(it.path)) }
        supabaseModules.forEach { module ->
            api(module.module + ":" + project.supabaseVersion)
        }
        serializers.forEach { serializer ->
            api("io.github.jan-tennert.supabase:" + serializer + ":" + project.supabaseVersion)
        }
    }
}
