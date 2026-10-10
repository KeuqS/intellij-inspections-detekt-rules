package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaConstructorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType

internal fun KaFunctionSymbol.assertionName(): String? =
    if (this is KaConstructorSymbol) {
        containingClassId?.run { shortClassName.asString() }
    } else {
        callableId?.run { callableName.asString() }
    }

context(session: KaSession)
internal fun KaFunctionSymbol.assertionOwners(): List<String> {
    val ownerClassId = (this as? KaConstructorSymbol)?.containingClassId ?: callableId?.classId
    val ownerClass = ownerClassId?.let { with(session) { findClass(it) } }
    val superTypes = ownerClass?.let { with(session) { it.defaultType.allSupertypes.toList() } }.orEmpty()
    return if (ownerClassId == null) {
        listOfNotNull(callableId?.run { packageName.asString() })
    } else {
        listOf(ownerClassId.asFqNameString()) +
            superTypes.filterIsInstance<KaClassType>().map { it.classId.asFqNameString() }
    }
}
