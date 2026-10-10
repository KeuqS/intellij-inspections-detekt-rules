package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration

private const val NESTED_MESSAGE = "Tests in nested class will not be executed"

context(session: KaSession)
internal fun KtClass.classMalformations(): List<Malformation> =
    if (isInterface() || isEnum() || isAnnotation() || isLocal || hasModifier(KtTokens.ABSTRACT_KEYWORD)) {
        emptyList()
    } else {
        (constructionMessages() + listOfNotNull(NESTED_MESSAGE.takeIf { isMalformedNested() })).map { malformation(it) }
    }

context(session: KaSession)
internal fun KtObjectDeclaration.objectMalformation(): Malformation? =
    with(session) {
        val isMalformed = hasDeclaredTests(ORG_JUNIT_TEST) && classSymbol?.hasRunWith() == false
        val anchor = nameIdentifier ?: getObjectKeyword() ?: this@objectMalformation
        val message = unconstructableMessage(name ?: "Companion", JUNIT4_CONSTRUCTOR_REASON)
        Malformation(anchor, message).takeIf { isMalformed }
    }
