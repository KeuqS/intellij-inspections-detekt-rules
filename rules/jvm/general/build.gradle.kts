plugins {
    id("detekt-rule-set")
}

dependencies {
    testRuntimeOnly(libs.jetbrains.annotations)
    testRuntimeOnly(libs.kotlinx.coroutines.core)
}
