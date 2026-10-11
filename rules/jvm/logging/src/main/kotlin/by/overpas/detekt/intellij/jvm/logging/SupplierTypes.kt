package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaFunctionType
import org.jetbrains.kotlin.analysis.api.types.KaType

context(session: KaSession)
internal fun KaType.functionalReturnType(): KaType? =
    when (val type = with(session) { lowerBoundIfFlexible() }) {
        is KaFunctionType -> type.returnType

        is KaClassType if (type.classId == JAVA_SUPPLIER || type.classId == LOG4J_SUPPLIER) ->
            type.typeArguments.firstOrNull()?.type

        else -> null
    }
