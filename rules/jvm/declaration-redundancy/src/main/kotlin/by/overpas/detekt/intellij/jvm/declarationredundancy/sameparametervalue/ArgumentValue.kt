package by.overpas.detekt.intellij.jvm.declarationredundancy.sameparametervalue

internal sealed interface ArgumentValue {

    fun presentation(): String?

    data object NotConstant : ArgumentValue {
        override fun presentation(): String? =
            null
    }

    data class Constant(val text: String) : ArgumentValue {
        override fun presentation(): String =
            text
    }

    data class Values(val values: List<ArgumentValue>) : ArgumentValue {
        override fun presentation(): String? =
            values.takeIf { NotConstant !in it }?.joinToString { it.presentation().orEmpty() }
    }
}

internal fun varargValue(values: List<ArgumentValue>): ArgumentValue =
    if (values.all { it == ArgumentValue.NotConstant }) ArgumentValue.NotConstant else ArgumentValue.Values(values)
