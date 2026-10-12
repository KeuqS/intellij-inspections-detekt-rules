package by.overpas.detekt.intellij.jvm.general.serializablehasserialversionuidfield

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration

@IntellijInspection("SerializableHasSerialVersionUIDField")
class SerializableHasSerialVersionUIDField(config: Config) :
    Rule(
        config,
        "A `Serializable` class does not define a `serialVersionUID` field. Without it, a change of the class " +
            "can break the deserialization of old data.",
    ),
    RequiresAnalysisApi {

    @Configuration("Do not report anonymous classes.")
    private val isAnonymousClassIgnored: Boolean by config(false)

    @Configuration("Do not report subclasses of these classes, unless they implement `Serializable` directly.")
    private val ignoredSuperClasses: List<String> by config(listOf("java.awt.Component"))

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
        val candidate = SerializableHasSerialVersionUIDFieldClass(classOrObject, ignoredSuperClasses)
        val isObjectLiteral = classOrObject is KtObjectDeclaration && classOrObject.isObjectLiteral()
        val isReported = candidate.isCandidate() &&
            !(isAnonymousClassIgnored && isObjectLiteral) &&
            !candidate.hasSerialVersionUid() &&
            analyze(classOrObject) { candidate.isSerializableWithoutUid() }
        if (isReported) {
            val anchor = (classOrObject as? KtObjectDeclaration)?.getObjectKeyword()
                ?.takeIf { isObjectLiteral }
            val entity = anchor?.let { Entity.from(it) } ?: Entity.atName(classOrObject)
            val name = if (isObjectLiteral) "object" else classOrObject.name.orEmpty()
            report(Finding(entity, "'$name' does not define a 'serialVersionUID' field"))
        }
    }
}
