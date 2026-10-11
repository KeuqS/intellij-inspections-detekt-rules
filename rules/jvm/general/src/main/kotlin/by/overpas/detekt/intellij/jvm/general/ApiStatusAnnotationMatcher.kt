package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotationValue
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol

internal class ApiStatusAnnotationMatcher(private val annotationNames: Collection<String>) {

    context(session: KaSession)
    fun findAnnotated(symbol: KaSymbol): ApiStatusAnnotatedDeclaration? =
        symbol.withApiStatusContainers().firstNotNullOfOrNull { container ->
            container.apiStatusAnnotations()
                .firstOrNull { it.classId?.asFqNameString() in annotationNames }
                ?.let { annotation ->
                    val version = annotation.arguments.firstOrNull { it.name.asString() == "inVersion" }?.expression
                    val constant = (version as? KaAnnotationValue.ConstantValue)?.value
                    ApiStatusAnnotatedDeclaration(
                        path = listOf(symbol, container).distinct()
                            .map { ApiStatusNamedSymbol(it.apiStatusName(), it.apiStatusKindName()) },
                        annotationName = annotation.classId?.asFqNameString().orEmpty(),
                        inVersion = constant?.value as? String,
                    )
                }
        }
}
