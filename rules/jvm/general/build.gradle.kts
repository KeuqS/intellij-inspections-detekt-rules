plugins {
    id("detekt-rule-set")
}

dependencies {
    testImplementation(libs.jetbrains.annotations)
    testRuntimeOnly(libs.kotlinx.coroutines.core)
}
