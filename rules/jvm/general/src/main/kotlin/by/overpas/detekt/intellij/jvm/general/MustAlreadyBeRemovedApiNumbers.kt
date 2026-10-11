package by.overpas.detekt.intellij.jvm.general

internal fun compareRemovalVersionNumbers(
    first: String,
    second: String,
): Int {
    val commonZeros = first.zip(second).takeWhile { (a, b) -> a == '0' && b == '0' }.size
    val left = first.drop(commonZeros)
    val right = second.drop(commonZeros)
    return when {
        left.startsWith('0') -> -1
        right.startsWith('0') -> 1
        left.length != right.length -> left.length.compareTo(right.length)
        else -> left.compareTo(right)
    }
}
