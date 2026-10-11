package by.overpas.detekt.intellij.android.weartilepreview

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.psi.KtParameter

context(session: KaSession)
internal fun KtParameter.hasContextType(): Boolean =
    with(session) { (typeReference?.type as? KaClassType)?.classId == CONTEXT }
