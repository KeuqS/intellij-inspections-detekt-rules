package by.overpas.detekt.intellij.android.composepreview.previewwrapperusage

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.android.composepreview.COMPOSABLE
import by.overpas.detekt.intellij.android.composepreview.resolvedClassId
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("PreviewWrapperUsage")
class PreviewWrapperUsage(config: Config) :
    Rule(
        config,
        "`@PreviewWrapper` is on a function that is not `@Composable` with `@Preview` or a MultiPreview, or a " +
            "function or an annotation class has more than one `@PreviewWrapper` in its annotation graph. " +
            "Compose Preview cannot apply the wrapper. Use one `@PreviewWrapper` on a composable preview function.",
    ),
    RequiresAnalysisApi {

    override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry) {
        super.visitAnnotationEntry(annotationEntry)
        val message = analyze(annotationEntry) {
            when {
                annotationEntry.isMisplacedPreviewWrapper() ->
                    "@PreviewWrapper can only be used on functions annotated with @Composable and @Preview " +
                        "or a MultiPreview"

                annotationEntry.isDuplicatePreviewWrapper() ->
                    "Only one @PreviewWrapper annotation is allowed on an annotation class"

                else -> null
            }
        }
        message?.let { report(Finding(Entity.from(annotationEntry), it)) }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        val wrappers = analyze(function) {
            if (function.annotationEntries.any { it.resolvedClassId() == COMPOSABLE }) {
                function.previewWrappersInGraph()
            } else {
                0
            }
        }
        if (wrappers > 1) {
            report(
                Finding(
                    Entity.atName(function),
                    "A function cannot have more than one @PreviewWrapper in its annotation graph (found $wrappers)",
                ),
            )
        }
    }

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        if (!klass.isAnnotation()) return
        val wrappers = analyze(klass) { klass.previewWrappersInGraph() }
        if (wrappers > 1) {
            report(
                Finding(
                    Entity.atName(klass),
                    "An annotation class cannot have more than one @PreviewWrapper in its annotation graph " +
                        "(found $wrappers)",
                ),
            )
        }
    }
}
