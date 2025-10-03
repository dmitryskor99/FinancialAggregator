import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import com.android.kotlin.multiplatform.ide.models.serialization.androidTargetKey

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

// такска detekt + lint
tasks.register("checkCodeQuality") {
    dependsOn(
        tasks.matching { it.name == "detekt" },      // проверка Kotlin кода
        tasks.matching { it.name.startsWith("lint") } // проверка всех Android модулей
    )
}

// Detekt
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

// Lint
subprojects {
    plugins.withId("com.android.application") {
        extensions.configure<CommonExtension<*, *, *, *, *, *>>("android") {
            configureCommonLint()
        }
    }

    plugins.withId("com.android.library") {
        extensions.configure<CommonExtension<*, *, *, *, *, *>>("android") {
            configureCommonLint()
        }
    }
}


fun CommonExtension<*, *, *, *, *, *>.configureCommonLint() {

    lint {
        textReport = true
        textOutput = layout.buildDirectory.file("reports/lint/lint-results.txt").get().asFile
        htmlReport = true
        htmlOutput = layout.buildDirectory.file("reports/lint/lint-results.html").get().asFile
        xmlReport = true
        xmlOutput = layout.buildDirectory.file("reports/lint/lint-results.xml").get().asFile
        checkReleaseBuilds = true
        abortOnError = true
        ignoreWarnings = false
        warningsAsErrors = true
    }
}