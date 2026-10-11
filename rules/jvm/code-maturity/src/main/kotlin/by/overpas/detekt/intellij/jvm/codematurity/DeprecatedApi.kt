package by.overpas.detekt.intellij.jvm.codematurity

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotated
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.name.ClassId

private const val JAVA_DEPRECATED_NAME = "java.lang.Deprecated"
private const val FOR_REMOVAL = "forRemoval"
private const val SINCE = "since"
private val JAVA_DEPRECATED = ClassId.fromString("java/lang/Deprecated")
private val KOTLIN_DEPRECATED = ClassId.fromString("kotlin/Deprecated")

internal class DeprecatedApi(symbol: KaSymbol) {

    private val annotations: List<KaAnnotation> = (symbol as? KaAnnotated)?.annotations.orEmpty()

    private val javaAnnotation: PsiAnnotation? = when (symbol) {
        is KaClassSymbol -> symbol.psi as? PsiClass
        is KaFunctionSymbol -> symbol.psi as? PsiMethod
        else -> symbol.psi as? PsiField
    }?.getAnnotation(JAVA_DEPRECATED_NAME)

    private val kotlinAnnotation: KaAnnotation? = annotations.firstOrNull { it.classId == JAVA_DEPRECATED }

    val isMarkedForRemoval: Boolean
        get() = javaAnnotation?.literalValue(FOR_REMOVAL) == true ||
            kotlinAnnotation?.constantValue(FOR_REMOVAL) == true

    val isDeprecated: Boolean
        get() = javaAnnotation != null ||
            annotations.any { it.classId == JAVA_DEPRECATED || it.classId == KOTLIN_DEPRECATED }

    val since: String?
        get() = (javaAnnotation?.literalValue(SINCE) ?: kotlinAnnotation?.constantValue(SINCE)) as? String
}
