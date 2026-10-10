plugins {
    id("detekt-rule-set")
}

dependencies {
    testRuntimeOnly(libs.assertj.core)
    testRuntimeOnly(libs.hamcrest)
    testRuntimeOnly(libs.junit4)
    testRuntimeOnly(libs.junit5.api)
    testRuntimeOnly(libs.junit5.params)
    testRuntimeOnly(libs.testng)
}
