package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId

context(session: KaSession)
internal fun ClassId.isTestAnnotation(): Boolean {
    val visited = mutableSetOf<ClassId>()
    val pending = ArrayDeque(listOf(this))
    val reachable = generateSequence { pending.removeFirstOrNull() }
        .filter { visited.add(it) }
        .onEach { classId ->
            pending += with(session) { findClass(classId) }?.annotations.orEmpty().mapNotNull { it.classId }
        }
    return this == ORG_JUNIT_TEST || this == TESTNG_TEST || reachable.any { it in JUPITER_TEST_ANNOTATIONS }
}
