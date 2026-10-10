package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.types.KaFlexibleType
import org.jetbrains.kotlin.analysis.api.types.KaType

internal fun KaType.lowerBound(): KaType =
    (this as? KaFlexibleType)?.lowerBound ?: this
