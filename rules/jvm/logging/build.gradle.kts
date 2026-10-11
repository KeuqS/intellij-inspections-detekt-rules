plugins {
    id("detekt-rule-set")
}

dependencies {
    testRuntimeOnly(libs.commons.logging)
    testRuntimeOnly(libs.log4j)
    testRuntimeOnly(libs.log4j2.api)
    testRuntimeOnly(libs.slf4j.api)
}
