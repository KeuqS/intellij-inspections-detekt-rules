package by.overpas.detekt.intellij.jvm.declarationredundancy

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.fileClasses.javaFileFacadeFqName
import org.jetbrains.kotlin.psi.KtDeclaration

context(session: KaSession)
internal fun KaVariableSymbol.presentableName(): String {
    val classId = callableId?.classId
    val owner = with(session) { containingSymbol } as? KaClassSymbol
    val ownerClassId = if (owner?.classKind == KaClassKind.COMPANION_OBJECT) classId?.outerClassId else classId
    val ownerName = ownerClassId?.run { shortClassName.asString() }
        ?: (psi as? KtDeclaration)?.run { containingKtFile.javaFileFacadeFqName.shortName().asString() }
    return listOfNotNull(ownerName, name.asString()).joinToString(".")
}
