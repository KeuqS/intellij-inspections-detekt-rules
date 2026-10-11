package by.overpas.detekt.intellij.jvm.general

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolOrigin
import org.jetbrains.kotlin.analysis.api.symbols.KaSyntheticJavaPropertySymbol
import org.jetbrains.kotlin.analysis.api.symbols.markers.KaAnnotatedSymbol
import org.jetbrains.kotlin.name.ClassId

context(session: KaSession)
internal fun KaSymbol.apiStatusTarget(): KaSymbol =
    when (this) {
        is KaSyntheticJavaPropertySymbol -> javaGetterSymbol
        is KaCallableSymbol -> with(session) { fakeOverrideOriginal }
        else -> this
    }

context(session: KaSession)
internal fun KaSymbol.withApiStatusContainers(): Sequence<KaSymbol> =
    generateSequence(this) { with(session) { it.containingDeclaration } }

internal fun KaSymbol.isApiStatusLibrary(): Boolean =
    origin == KaSymbolOrigin.LIBRARY || origin == KaSymbolOrigin.JAVA_LIBRARY

internal fun KaSymbol.apiStatusAnnotations(): List<KaAnnotation> =
    when (this) {
        is KaPropertySymbol -> buildList {
            addAll(annotations)
            addAll(backingFieldSymbol?.annotations.orEmpty())
            addAll(getter?.annotations.orEmpty())
        }

        is KaAnnotatedSymbol -> annotations.toList()

        else -> emptyList()
    }

internal fun KaSymbol.hasApiStatusAnnotation(classId: ClassId): Boolean =
    apiStatusAnnotations().any { it.classId == classId }

internal fun KaSymbol.isDeclaredInFileOf(element: PsiElement): Boolean =
    psi?.containingFile?.let { it == element.containingFile } == true
