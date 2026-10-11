package by.overpas.detekt.intellij.jvm.general.serializablehasserialversionuidfield

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaFlexibleType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtProperty

private const val SERIAL_VERSION_UID = "serialVersionUID"

private val SERIALIZABLE = ClassId.fromString("java/io/Serializable")

private val JVM_RECORD = ClassId.fromString("kotlin/jvm/JvmRecord")

private val WRITE_REPLACE = Name.identifier("writeReplace")

internal class SerializableHasSerialVersionUIDFieldClass(
    private val classOrObject: KtClassOrObject,
    private val ignoredSuperClasses: List<String>,
) {

    fun isCandidate(): Boolean {
        val klass = classOrObject as? KtClass
        return classOrObject !is KtEnumEntry &&
            klass?.isInterface() != true &&
            klass?.isAnnotation() != true &&
            klass?.isEnum() != true
    }

    fun hasSerialVersionUid(): Boolean {
        val companions = classOrObject.companionObjects
        return (listOf(classOrObject) + companions).any { owner ->
            owner.declarations.any { it is KtProperty && it.name == SERIAL_VERSION_UID }
        }
    }

    context(session: KaSession)
    fun isSerializableWithoutUid(): Boolean =
        with(session) {
            val symbol = classOrObject.symbol as? KaClassSymbol
            val serializable = findClass(SERIALIZABLE)
            symbol != null &&
                serializable != null &&
                symbol.annotations.none { it.classId == JVM_RECORD } &&
                symbol.isSubClassOf(serializable) &&
                !symbol.hasWriteReplace() &&
                !symbol.isIgnoredSubclass()
        }

    context(session: KaSession)
    private fun KaClassSymbol.hasWriteReplace(): Boolean =
        with(session) {
            memberScope.callables(WRITE_REPLACE).filterIsInstance<KaNamedFunctionSymbol>().any { function ->
                val type = function.returnType
                val returnType = ((type as? KaFlexibleType)?.lowerBound ?: type) as? KaClassType
                function.valueParameters.isEmpty() &&
                    function.receiverParameter == null &&
                    returnType?.classId == StandardClassIds.Any
            }
        }

    context(session: KaSession)
    private fun KaClassSymbol.isIgnoredSubclass(): Boolean =
        with(session) {
            val isDirectlySerializable = superTypes.any { (it as? KaClassType)?.classId == SERIALIZABLE }
            !isDirectlySerializable &&
                ignoredSuperClasses.mapNotNull { findClass(ClassId.topLevel(FqName(it))) }.any { isSubClassOf(it) }
        }
}
