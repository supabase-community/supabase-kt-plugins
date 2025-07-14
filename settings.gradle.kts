
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.10.0")
}

// Test module
include("test-common")

// Misc plugins
include(":bom")
include(":ApolloGraphQL")
include(":ComposeAuth")
include(":ComposeAuthUI")
include(":Coil3Integration")
include(":CoilIntegration")
include(":ImageLoaderIntegration")
//include(":SketchIntegration")

// Renames
project(":ApolloGraphQL").name = "apollo-graphql"
project(":ComposeAuth").name = "compose-auth"
project(":ComposeAuthUI").name = "compose-auth-ui"
project(":Coil3Integration").name = "coil3-integration"
project(":CoilIntegration").name = "coil-integration"
project(":ImageLoaderIntegration").name = "imageloader-integration"
//project(":SketchIntegration").name = "sketch-integration"
rootProject.name = "supabase-kt-plugins"