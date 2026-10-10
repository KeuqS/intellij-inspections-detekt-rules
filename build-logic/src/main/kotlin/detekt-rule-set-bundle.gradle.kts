plugins {
    java
    id("com.gradleup.shadow")
}

val ruleSetGroup = project.name
val ruleSetGroupPath = ":rules:$ruleSetGroup"

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
