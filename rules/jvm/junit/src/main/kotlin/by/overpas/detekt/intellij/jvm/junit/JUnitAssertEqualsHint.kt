package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall

context(session: KaSession)
internal fun KaFunctionCall<*>.assertEqualsHint(): JUnitAssertHint? =
    assertHint { if (it == "assertEquals") 2 else null }
