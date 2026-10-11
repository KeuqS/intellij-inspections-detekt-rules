package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaFlexibleType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.StandardClassIds

private val URL_CLASS_ID = ClassId.fromString("java/net/URL")

private val URL_COLLECTIONS = setOf(
    StandardClassIds.Map,
    StandardClassIds.MutableMap,
    StandardClassIds.Set,
    StandardClassIds.MutableSet,
    ClassId.fromString("java/util/Map"),
    ClassId.fromString("java/util/Set"),
)

internal class UrlHashCodeType(type: KaType?) {

    private val classType = ((type as? KaFlexibleType)?.lowerBound ?: type) as? KaClassType

    fun isUrl(): Boolean =
        classType?.classId == URL_CLASS_ID

    fun isUrlCollection(): Boolean {
        val arguments = classType?.typeArguments.orEmpty().map { UrlHashCodeType(it.type) }
        return (classType?.classId in URL_COLLECTIONS && arguments.firstOrNull()?.isUrl() == true) ||
            arguments.any { it.isUrlCollection() }
    }
}
