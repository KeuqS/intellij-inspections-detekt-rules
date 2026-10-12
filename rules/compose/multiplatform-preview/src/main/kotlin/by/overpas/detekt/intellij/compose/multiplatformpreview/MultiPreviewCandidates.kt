package by.overpas.detekt.intellij.compose.multiplatformpreview

import org.jetbrains.kotlin.name.ClassId

private val COMPOSE_PREFIXES = listOf("androidx.", "org.jetbrains.compose.")
private val NON_MULTIPREVIEW_PREFIXES = listOf("android.", "kotlin.", "kotlinx.", "java.")

internal fun ClassId.couldBeMultiPreview(): Boolean {
    val name = asFqNameString()
    return if (COMPOSE_PREFIXES.any { name.startsWith(it) }) {
        ".preview." in name
    } else {
        NON_MULTIPREVIEW_PREFIXES.none { name.startsWith(it) }
    }
}
