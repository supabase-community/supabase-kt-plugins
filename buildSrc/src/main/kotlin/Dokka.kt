import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaExtension
import java.net.URI

fun Project.applyDokkaWithConfiguration() {
    extensions.configure(DokkaExtension::class) {
        val name = when(moduleName.get()) {
            "apollo-graphql" -> "ApolloGraphQL"
            "compose-auth" -> "ComposeAuth"
            "compose-auth-ui" -> "ComposeAuthUI"
            "coil-integration" -> "CoilIntegration"
            "coil3-integration" -> "Coil3Integration"
            "imageloader-integration" -> "ImageLoaderIntegration"
            else -> ""
        }
        dokkaSourceSets.configureEach {
      //      includes.from("README.md")
            sourceLink {
                localDirectory.set(projectDir.resolve("src"))
                remoteUrl.set(URI("https://github.com/supabase-community/supabase-kt-plugins/tree/master/$name/src"))
                remoteLineSuffix.set("#L")
            }
        }
    }
}