package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

internal class LevelDisagreement(
    guard: KtExpression,
    val conditionLevel: String,
    val callLevel: String,
) {

    val anchor: KtExpression = (guard as? KtCallExpression)?.calleeExpression ?: guard
}
