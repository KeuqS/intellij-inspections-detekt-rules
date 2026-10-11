package by.overpas.detekt.intellij.jvm.general

internal class ApiStatusAnnotatedDeclaration(
    private val path: List<ApiStatusNamedSymbol>,
    val annotationName: String,
    val inVersion: String?,
) {

    val target: ApiStatusNamedSymbol
        get() = path.first()

    val container: ApiStatusNamedSymbol
        get() = path.last()

    val isOwnAnnotation: Boolean
        get() = path.size == 1

    val presentableAnnotationName: String
        get() = annotationName.split('.')
            .takeLastWhile { it.isNotEmpty() && it.first().isUpperCase() }
            .joinToString(".")
            .ifEmpty { annotationName }
}
