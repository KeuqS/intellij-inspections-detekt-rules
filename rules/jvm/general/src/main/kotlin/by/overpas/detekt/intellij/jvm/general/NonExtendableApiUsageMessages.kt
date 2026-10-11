package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

context(session: KaSession)
internal fun ApiStatusUsage.nonExtendableSuperTypeMessage(): String? {
    val superClass = (target as? KaClassSymbol)?.takeIf { symbol ->
        kind == ApiStatusUsage.Kind.REFERENCE &&
            symbol.isApiStatusLibrary() &&
            symbol.hasApiStatusAnnotation(API_STATUS_NON_EXTENDABLE)
    }
    val entry = element.getParentOfType<KtSuperTypeListEntry>(true)
    val entryClassId = with(session) { entry?.typeReference?.type?.expandedSymbol?.classId }
    val isInterfaceDeclaration = (entry?.getParentOfType<KtClassOrObject>(true) as? KtClass)?.isInterface() == true
    return when {
        superClass == null || entryClassId != superClass.classId -> null
        superClass.classKind != KaClassKind.INTERFACE -> "Class '${superClass.apiStatusName()}' must not be extended"
        isInterfaceDeclaration -> "Interface '${superClass.apiStatusName()}' must not be extended"
        else -> "Interface '${superClass.apiStatusName()}' must not be implemented"
    }
}

internal fun ApiStatusUsage.nonExtendableOverrideMessage(): String? =
    "Method '${target.apiStatusName(isQualified = false)}' must not be overridden".takeIf {
        kind == ApiStatusUsage.Kind.OVERRIDE &&
            target.isApiStatusLibrary() &&
            target.hasApiStatusAnnotation(API_STATUS_NON_EXTENDABLE)
    }
