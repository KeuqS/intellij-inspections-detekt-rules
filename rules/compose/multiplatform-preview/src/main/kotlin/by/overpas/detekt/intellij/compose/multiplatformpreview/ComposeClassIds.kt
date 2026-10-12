package by.overpas.detekt.intellij.compose.multiplatformpreview

import org.jetbrains.kotlin.name.ClassId

internal val COMPOSABLE = ClassId.fromString("androidx/compose/runtime/Composable")
internal val PREVIEWS = setOf(
    ClassId.fromString("androidx/compose/ui/tooling/preview/Preview"),
    ClassId.fromString("org/jetbrains/compose/ui/tooling/preview/Preview"),
)
internal val PREVIEW_PARAMETERS = setOf(
    ClassId.fromString("androidx/compose/ui/tooling/preview/PreviewParameter"),
    ClassId.fromString("org/jetbrains/compose/ui/tooling/preview/PreviewParameter"),
)
