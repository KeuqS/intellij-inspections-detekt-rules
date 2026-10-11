package by.overpas.detekt.intellij.jvm.general.usagesofobsoleteapi

import by.overpas.detekt.intellij.jvm.general.API_STATUS_OBSOLETE
import by.overpas.detekt.intellij.jvm.general.ApiStatusUsage
import by.overpas.detekt.intellij.jvm.general.hasApiStatusAnnotation
import by.overpas.detekt.intellij.jvm.general.isDeclaredInFileOf
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.api.symbols.KaClassLikeSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol

internal fun List<ApiStatusUsage>.obsoleteElements(): List<PsiElement> =
    asSequence()
        .filter { it.isObsoleteUsage() }
        .map { it.element }
        .distinct()
        .toList()

private fun ApiStatusUsage.isObsoleteUsage(): Boolean =
    kind != ApiStatusUsage.Kind.IMPORT &&
        (target is KaClassLikeSymbol || target is KaFunctionSymbol || target is KaVariableSymbol) &&
        !target.isDeclaredInFileOf(element) &&
        target.hasApiStatusAnnotation(API_STATUS_OBSOLETE)
