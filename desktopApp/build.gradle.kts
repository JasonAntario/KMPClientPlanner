import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(projects.sharedUI)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)

    implementation(libs.koin.core)
    implementation(libs.koin.compose)
}

compose.desktop {
    application {
        mainClass = "com.dsankovsky.kmpclientplanner.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.dsankovsky.kmpclientplanner"
            packageVersion = "0.1.0"

            windows{
                menuGroup = "Client Planner"
            }
        }
    }
}