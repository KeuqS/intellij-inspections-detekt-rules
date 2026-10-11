package by.overpas.detekt.intellij.gradle.bestpractices.avoidduplicaterepositories

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.gradle.bestpractices.isGradleScript
import by.overpas.detekt.intellij.gradle.bestpractices.lambdaBody
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

@IntellijInspection("AvoidDuplicateRepositories")
class AvoidDuplicateRepositories(config: Config) :
    Rule(
        config,
        "A `repositories` block declares the same repository more than one time. Remove the duplicates.",
    ) {

    override fun visitKtFile(file: KtFile) {
        if (file.isGradleScript()) super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.calleeExpression?.text != "repositories") return
        val repositories = expression.lambdaBody()?.statements.orEmpty().filterIsInstance<KtCallExpression>()
        repositories.groupBy { it.normalizedText() }
            .filterValues { it.size > 1 }
            .forEach { (text, duplicates) ->
                val name = if (text.length > MAX_NAME_LENGTH) text.take(MAX_NAME_LENGTH) + "..." else text
                duplicates.forEach { repository ->
                    report(
                        Finding(
                            Entity.from(repository.calleeExpression ?: repository),
                            "Repository '$name' is declared multiple times",
                        ),
                    )
                }
            }
    }

    private fun PsiElement.normalizedText(): String =
        collectDescendantsOfType<PsiElement> { it.firstChild == null && it !is PsiWhiteSpace && it !is PsiComment }
            .joinToString("") { it.text }

    private companion object {
        const val MAX_NAME_LENGTH = 20
    }
}
