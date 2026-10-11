package by.overpas.detekt.intellij.jvm.general.suppressionannotation

import by.overpas.detekt.intellij.IntellijInspection
import com.intellij.psi.PsiComment
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtAnnotationEntry

@IntellijInspection("SuppressionAnnotation")
class SuppressionAnnotation(config: Config) :
    Rule(
        config,
        "A `@Suppress` or `@SuppressWarnings` annotation or a `//noinspection` comment hides inspection warnings. " +
            "Fix the code instead.",
    ),
    RequiresAnalysisApi {

    @Configuration("The suppressed ids that are allowed.")
    private val allowedSuppressions: Set<String> by config(emptyList<String>()) { it.toSet() }

    override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry) {
        super.visitAnnotationEntry(annotationEntry)
        val isCandidate = annotationEntry.shortName?.asString() in SUPPRESSION_ANNOTATION_NAMES
        val ids = annotationEntry.takeIf { isCandidate }
            ?.let { analyze(it) { SuppressionAnnotationIds(it).ids() } }
        if (ids != null && (ids.isEmpty() || ids.any { it !in allowedSuppressions })) {
            val anchor = annotationEntry.typeReference ?: annotationEntry
            report(Finding(Entity.from(anchor), "Annotation suppresses ${ids.suppressionAnnotationList()}".trim()))
        }
    }

    override fun visitComment(comment: PsiComment) {
        super.visitComment(comment)
        val ids = comment.suppressionCommentIds() ?: return
        if (ids.isEmpty() || ids.any { it !in allowedSuppressions }) {
            report(Finding(Entity.from(comment), "Comment suppresses ${ids.suppressionAnnotationList()}".trim()))
        }
    }
}
