import org.gradle.accessors.dm.LibrariesForLibs
import java.util.zip.ZipFile

plugins {
    id("code-coverage")
    id("com.gradleup.shadow")
    id("jvm-lib")
    id("static-analysis")
}

val libs = the<LibrariesForLibs>()

dependencies {
    compileOnly(project(":annotations"))
    compileOnly(libs.detekt.api)

    testImplementation(libs.detekt.api)
    testImplementation(libs.detekt.test)
    testImplementation(libs.kotlin.test)
}

tasks.shadowJar {
    archiveBaseName = "intellij-inspections-${project.path.removePrefix(":rules:").replace(':', '-')}"
    archiveClassifier = ""
    archiveVersion = project.version.toString()
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    manifest {
        attributes(
            "Implementation-Title" to archiveBaseName.get(),
            "Implementation-Version" to archiveVersion.get(),
        )
    }
}

val verifyDefaultConfig by tasks.registering {
    group = "verification"
    description = "Checks that the rule set jar contains the default config."
    val jar = tasks.shadowJar.flatMap { it.archiveFile }
    inputs.file(jar)
    doLast {
        ZipFile(jar.get().asFile).use { zip ->
            check(zip.getEntry("config/config.yml") != null) {
                "${jar.get().asFile.name} does not contain config/config.yml"
            }
        }
    }
}

tasks.check {
    dependsOn(verifyDefaultConfig)
}
