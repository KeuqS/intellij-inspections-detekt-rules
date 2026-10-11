package by.overpas.detekt.intellij.android.composepreview

import org.jetbrains.kotlin.name.ClassId

private val NON_MULTIPREVIEW_PREFIXES = listOf("android.", "kotlin.", "kotlinx.", "java.")

internal fun ClassId.couldBeMultiPreview(): Boolean {
    val name = asFqNameString()
    return if (name.startsWith("androidx.")) {
        ".preview." in name
    } else {
        NON_MULTIPREVIEW_PREFIXES.none { name.startsWith(it) }
    }
}
