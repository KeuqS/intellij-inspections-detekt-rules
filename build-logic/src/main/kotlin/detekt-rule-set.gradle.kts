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

val verifyUpstreamDefaults by tasks.registering {
    group = "verification"
    description = "Checks that the default config does not activate the rules that IntelliJ IDEA disables by default."
    val modulePath = project.path
    val catalog = rootProject.layout.projectDirectory.file("docs/inspections.csv")
    val config = layout.projectDirectory.file("src/main/resources/config/config.yml")
    inputs.file(catalog)
    inputs.file(config)
    doLast {
        val column = Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")
        val rows = catalog.asFile.readLines().map { it.split(column) }
        val header = rows.first()
        val upstreamDisabled = rows.drop(1)
            .map { row -> header.zip(row).toMap() }
            .filter { it["module"] == modulePath && it["enabledByDefault"] == "false" }
            .filter { it["status"].orEmpty().startsWith("ported:") }
            .flatMap { row -> Regex("`(\\w+)`").findAll(row["status"].orEmpty()).map { it.groupValues[1] } }
        val ruleEntry = Regex("^ {2}(\\w+):$")
        val activeEntry = Regex("^ {4}active: (\\w+)$")
        val activeRules = buildSet {
            var rule: String? = null
            config.asFile.readLines().forEach { line ->
                ruleEntry.find(line)?.let { rule = it.groupValues[1] }
                activeEntry.find(line)?.takeIf { it.groupValues[1] == "true" }?.let { rule?.let(::add) }
            }
        }
        val violations = upstreamDisabled.filter { it in activeRules }
        check(violations.isEmpty()) {
            "$modulePath activates rules that IntelliJ IDEA disables by default: ${violations.joinToString()}"
        }
    }
}

tasks.check {
    dependsOn(verifyDefaultConfig, verifyUpstreamDefaults)
}
