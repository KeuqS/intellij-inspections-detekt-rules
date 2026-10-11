package by.overpas.detekt.intellij.jvm.codematurity.markedforremoval

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiLiteral
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotationValue

internal fun PsiAnnotation.literalValue(name: String): Any? =
    (findDeclaredAttributeValue(name) as? PsiLiteral)?.value

internal fun KaAnnotation.constantValue(name: String): Any? {
    val expression = arguments.firstOrNull { it.name.asString() == name }?.expression
    return (expression as? KaAnnotationValue.ConstantValue)?.run { value.value }
}
