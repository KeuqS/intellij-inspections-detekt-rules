import java.util.zip.ZipFile

plugins {
    java
    id("com.gradleup.shadow")
}

val ruleSetGroup = project.name
val ruleSetGroupPath = ":rules:$ruleSetGroup"
val ruleSetIds = rootProject.subprojects
    .filter { it.parent?.path == ruleSetGroupPath }
    .map { "intellij-$ruleSetGroup-${it.name}" }
    .sorted()

dependencies {
    rootProject.subprojects
        .filter { it.parent?.path == ruleSetGroupPath }
        .sortedBy { it.path }
        .forEach { implementation(project(it.path)) }
}

tasks.shadowJar {
    archiveBaseName = "intellij-inspections-$ruleSetGroup-all"
    archiveClassifier = ""
    archiveVersion = project.version.toString()
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    filesMatching(listOf("META-INF/services/**", "config/config.yml")) {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    mergeServiceFiles()
    append("config/config.yml")
    manifest {
        attributes(
            "Implementation-Title" to archiveBaseName.get(),
            "Implementation-Version" to archiveVersion.get(),
        )
    }
}

val verifyDefaultConfig by tasks.registering {
    group = "verification"
    description = "Checks that the bundle jar contains the default config of every rule set."
    val jar = tasks.shadowJar.flatMap { it.archiveFile }
    val expectedIds = ruleSetIds
    inputs.file(jar)
    inputs.property("ruleSetIds", expectedIds)
    doLast {
        val configuredIds = ZipFile(jar.get().asFile).use { zip ->
            val entry = checkNotNull(zip.getEntry("config/config.yml")) {
                "${jar.get().asFile.name} does not contain config/config.yml"
            }
            zip.getInputStream(entry).bufferedReader().readLines()
                .mapNotNull { Regex("^([\\w-]+):$").find(it)?.groupValues?.get(1) }
                .sorted()
        }
        check(configuredIds == expectedIds) {
            "${jar.get().asFile.name} configures $configuredIds instead of $expectedIds"
        }
    }
}

tasks.check {
    dependsOn(verifyDefaultConfig)
}
