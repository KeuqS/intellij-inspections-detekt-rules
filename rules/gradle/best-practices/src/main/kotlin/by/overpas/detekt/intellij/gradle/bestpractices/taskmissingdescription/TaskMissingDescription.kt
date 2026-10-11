package by.overpas.detekt.intellij.gradle.bestpractices.taskmissingdescription

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.gradle.bestpractices.enclosingBlockName
import by.overpas.detekt.intellij.gradle.bestpractices.isGradleScript
import by.overpas.detekt.intellij.gradle.bestpractices.lambdaBody
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

@IntellijInspection("TaskMissingDescription")
class TaskMissingDescription(config: Config) :
    Rule(
        config,
        "A task is registered without a description. Set the `description` of the task, so that `gradle tasks` " +
            "explains it.",
    ) {

    override fun visitKtFile(file: KtFile) {
        if (file.isGradleScript()) super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val callee = expression.calleeExpression ?: return
        if (callee.text !in TASK_FACTORIES || !expression.isOnTaskContainer()) return
        if (expression.lambdaBody()?.hasDescription() != true) {
            report(Finding(Entity.from(callee), MESSAGE))
        }
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        if (expression !is KtNameReferenceExpression || expression.parent is KtCallExpression) return
        if (expression.getReferencedName() in TASK_DELEGATES && expression.isOnTaskContainer()) {
            report(Finding(Entity.from(expression), MESSAGE))
        }
    }

    private fun KtExpression.isOnTaskContainer(): Boolean {
        val qualified = parent as? KtDotQualifiedExpression
        return if (qualified?.selectorExpression == this) {
            qualified.receiverExpression.text.substringAfterLast('.') == "tasks"
        } else {
            enclosingBlockName() == "tasks"
        }
    }

    private fun KtBlockExpression.hasDescription(): Boolean =
        anyDescendantOfType<KtBinaryExpression> {
            it.operationToken == KtTokens.EQ && it.left?.text == "description"
        } ||
            anyDescendantOfType<KtCallExpression> { it.calleeExpression?.text == "setDescription" }

    private companion object {
        const val MESSAGE = "Task is missing a description"
        val TASK_DELEGATES = setOf("registering", "creating")
        val TASK_FACTORIES = setOf("register", "create") + TASK_DELEGATES
    }
}
