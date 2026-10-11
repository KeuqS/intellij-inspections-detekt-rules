package by.overpas.detekt.intellij.jvm.general

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("UnstableTypeUsedInSignature")
class UnstableTypeUsedInSignature(config: Config) :
    Rule(
        config,
        "A public declaration refers to a type that an unstable API annotation marks, but the declaration " +
            "itself is not marked with that annotation. Mark the declaration with the same annotation.",
    ),
    RequiresAnalysisApi {

    @Configuration("The fully qualified names of the annotations that mark an API as unstable.")
    private val unstableApiAnnotations: List<String> by config(UNSTABLE_API_ANNOTATIONS)

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        if (!dcl.isApiStatusDeclaration() || !dcl.isUnstableTypeCandidate()) return
        val names = unstableApiAnnotations.toSet()
        val signatureMatcher = ApiStatusSignatureMatcher(ApiStatusAnnotationMatcher(names))
        val annotated = analyze(dcl) {
            signatureMatcher.find(dcl.symbol).takeUnless { dcl.isInsideUnstableDeclaration(names) }
        } ?: return
        val (kind, part) = when (dcl) {
            is KtClassOrObject -> "Class" to "declaration"
            is KtNamedFunction, is KtConstructor<*> -> "Method" to "signature"
            else -> "Field" to "type"
        }
        val message = "$kind must be marked with '@${annotated.annotationName}' annotation because its $part " +
            "references unstable type '${annotated.target.name}'"
        report(Finding(Entity.from(dcl.apiStatusAnchor()), message))
    }
}
