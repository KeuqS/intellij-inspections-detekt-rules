package by.overpas.detekt.intellij.gradle.bestpractices

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtScript
import org.jetbrains.kotlin.psi.KtScriptInitializer
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

internal fun KtFile.isGradleScript(): Boolean =
    name.endsWith(".gradle.kts")

internal fun KtCallExpression.lambdaBody(): KtBlockExpression? =
    lambdaArguments.lastOrNull()?.getLambdaExpression()?.bodyExpression
        ?: (valueArguments.singleOrNull()?.getArgumentExpression() as? KtLambdaExpression)?.bodyExpression

internal fun KtElement.enclosingBlockName(): String? =
    getStrictParentOfType<KtFunctionLiteral>()?.getStrictParentOfType<KtCallExpression>()?.calleeExpression?.text

internal fun KtScript.topLevelBlocks(name: String): List<KtBlockExpression> =
    declarations.asSequence()
        .filterIsInstance<KtScriptInitializer>()
        .mapNotNull { it.body as? KtCallExpression }
        .filter { it.calleeExpression?.text == name }
        .mapNotNull { it.lambdaBody() }
        .toList()
