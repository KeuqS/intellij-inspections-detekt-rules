package by.overpas.detekt.intellij.jvm.logging

internal fun Pair<PartIterator, PartIterator>.equalStep(): Int {
    val length = first.current.length
    first.move()
    second.move()
    return length
}

internal fun Pair<PartIterator, PartIterator>.prefixStep(): Int? {
    val firstText = first.current
    val secondText = second.current
    return when {
        firstText.startsWith(secondText) -> secondText.length.also { length ->
            second.move()
            first.move(length)
        }

        secondText.startsWith(firstText) -> firstText.length.also { length ->
            first.move()
            second.move(length)
        }

        else -> null
    }
}

internal fun Pair<PartIterator, PartIterator>.suffixStep(): Int? {
    val firstText = first.current
    val secondText = second.current
    val length = when {
        firstText.endsWith(secondText) -> secondText.length
        secondText.endsWith(firstText) -> firstText.length
        else -> null
    }
    if (length != null) {
        first.move()
        second.move()
    }
    return length
}

internal fun Pair<PartIterator, PartIterator>.containedStep(): Int? {
    val firstText = first.current
    val secondText = second.current
    val secondIndex = firstText.indexOf(secondText).takeIf { second.isPreviousUnknown && it != -1 }
    val firstIndex = secondText.indexOf(firstText).takeIf { secondIndex == null && first.isPreviousUnknown && it != -1 }
    return when {
        secondIndex != null -> (secondIndex + secondText.length).also { length ->
            second.move()
            first.move(length)
        }

        firstIndex != null -> (firstIndex + firstText.length).also { length ->
            first.move()
            second.move(length)
        }

        else -> null
    }
}
