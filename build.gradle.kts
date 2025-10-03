plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.detekt) apply true
}

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
        config.setFrom(files("$rootDir/detekt-config.yml"))
        buildUponDefaultConfig = true
        parallel = true
        tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
            reports {
                html.required.set(true)
                xml.required.set(true)
            }
        }
    }
}