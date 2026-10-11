package by.overpas.detekt.intellij.jvm.logging

internal data class PartHolder(
    val text: String?,
    val isConstant: Boolean,
    val callArguments: List<String>? = null,
) {

    val isText: Boolean
        get() = isConstant && text != null

    val isNotBlankText: Boolean
        get() = isConstant && text?.isNotBlank() == true

    companion object {
        val UNKNOWN = PartHolder(null, isConstant = false)
        val NON_TEXT_CONSTANT = PartHolder(null, isConstant = true)
    }
}

internal fun PartHolder.splitWithPlaceholders(): List<PartHolder> {
    val pieces = text.orEmpty().split("{}")
    return if (!isText) {
        listOf(this)
    } else {
        pieces.flatMapIndexed { index, piece ->
            val textPart = PartHolder(piece, isConstant = true)
            if (index == pieces.lastIndex) listOf(textPart) else listOf(textPart, PartHolder(piece, isConstant = false))
        }
    }
}

internal fun List<PartHolder>.joined(): List<PartHolder> =
    fold(mutableListOf()) { result, holder ->
        val last = result.lastOrNull()
        if (holder.isText && last != null && last.isText) {
            result[result.lastIndex] = PartHolder(last.text + holder.text, isConstant = true)
        } else {
            result += holder
        }
        result
    }
