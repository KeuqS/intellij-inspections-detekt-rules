package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.AFTER_ALL_CALLBACK
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.AFTER_EACH_CALLBACK
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.BEFORE_ALL_CALLBACK
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.BEFORE_EACH_CALLBACK
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.REGISTER_EXTENSION
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

context(session: KaSession)
internal fun KtProperty.fieldMessages(ids: List<ClassId>): List<String> =
    with(session) {
        FIELD_REQUIREMENTS.mapNotNull { requirement ->
            requirement.annotations.firstOrNull { it in ids }?.let { annotation ->
                SignatureProblems(
                    modifiers = listOfNotNull(
                        requirement.staticProblem(isFieldStatic(), isPerClass = false),
                        requirement.visibilityProblem(isFieldPublic(), isPrivateMember()),
                    ),
                    type = requirement.typeProblem(symbol.returnType),
                    parameters = emptyList(),
                ).takeUnless { it.isEmpty() }?.message("Field '${name.orEmpty()}'", annotation)
            }
        }
    }

context(session: KaSession)
internal fun KtProperty.getterMessages(ids: List<ClassId>): List<String> =
    with(session) {
        METHOD_REQUIREMENTS.mapNotNull { requirement ->
            requirement.annotations.firstOrNull { it in ids }?.let { annotation ->
                SignatureProblems(
                    modifiers = listOfNotNull(
                        requirement.staticProblem(isJvmStatic(), isPerClass = false),
                        requirement.visibilityProblem(isPublicMember(), isPrivateMember()),
                    ),
                    type = requirement.typeProblem(symbol.returnType),
                    parameters = emptyList(),
                ).takeUnless { it.isEmpty() }?.message("Method '${name.orEmpty()}'", annotation)
            }
        }
    }

context(session: KaSession)
internal fun KtProperty.extensionLevelMessage(ids: List<ClassId>): String? =
    with(session) {
        val type = symbol.returnType
        val isClassLevel = type.isSubtypeOf(BEFORE_ALL_CALLBACK) || type.isSubtypeOf(AFTER_ALL_CALLBACK)
        val isInstanceLevel = type.isSubtypeOf(BEFORE_EACH_CALLBACK) || type.isSubtypeOf(AFTER_EACH_CALLBACK)
        val isMalformed = REGISTER_EXTENSION in ids &&
            !isFieldStatic() &&
            isClassLevel &&
            !isInstanceLevel &&
            containingClassOrObject?.isPerClass() != true
        val typeName = type.expandedSymbol?.name?.asString()
        typeName?.takeIf { isMalformed }?.let { "$it should be registered at the class level" }
    }
