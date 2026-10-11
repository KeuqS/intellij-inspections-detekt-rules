package by.overpas.detekt.intellij.jvm.general

internal enum class BlockingMethodInNonBlockingContextType {
    UNSURE,
    BLOCKING,
    NON_BLOCKING,
}
