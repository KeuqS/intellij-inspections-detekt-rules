package by.overpas.detekt.intellij.jvm.junit.parameterizedparametersstaticcollection

import by.overpas.detekt.intellij.jvm.junit.hasAnnotationEntry
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isPublic

private val JVM_STATIC = ClassId.fromString("kotlin/jvm/JvmStatic")

context(session: KaSession)
internal fun KtNamedFunction.hasDataProviderSignature(): Boolean {
    val isStatic = (containingClassOrObject as? KtObjectDeclaration)?.isCompanion() == true &&
        hasAnnotationEntry(JVM_STATIC)
    val returnType = with(session) { symbol.returnType }
    return isPublic && isStatic && (
        with(
            session,
        ) { returnType.isSubtypeOf(StandardClassIds.Iterable) } || returnType.isObjectArray()
        )
}

context(session: KaSession)
private fun KaType.isObjectArray(): Boolean =
    with(session) {
        val deepest = generateSequence(arrayElementType) { it.arrayElementType }.lastOrNull()
        isArrayOrPrimitiveArray && deepest?.isAnyType == true
    }
