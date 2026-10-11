package by.overpas.detekt.intellij.jvm.general

import dev.detekt.api.Entity
import dev.detekt.api.Finding
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallableDeclaration

internal class UrlHashCodeDeclaration(private val declaration: KtCallableDeclaration) {

    fun finding(): Finding? {
        val isReported = declaration.nameIdentifier != null &&
            analyze(declaration) { UrlHashCodeType(declaration.returnType).isUrlCollection() }
        return Finding(Entity.atName(declaration), "'${declaration.name.orEmpty()}' may contain URL objects")
            .takeIf { isReported }
    }
}
