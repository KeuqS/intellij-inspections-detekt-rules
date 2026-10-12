package by.overpas.detekt.intellij.jvm.general.blockingmethodinnonblockingcontext

internal enum class BlockingMethodInNonBlockingContextDispatcher {
    NO_INFORMATION,
    UNKNOWN,
    NON_BLOCKING,
    BLOCKING,
    ;

    fun isDefinite(): Boolean =
        this == NON_BLOCKING || this == BLOCKING

    fun asType(default: BlockingMethodInNonBlockingContextType): BlockingMethodInNonBlockingContextType =
        when (this) {
            NO_INFORMATION -> default
            UNKNOWN -> BlockingMethodInNonBlockingContextType.UNSURE
            NON_BLOCKING -> BlockingMethodInNonBlockingContextType.NON_BLOCKING
            BLOCKING -> BlockingMethodInNonBlockingContextType.BLOCKING
        }
}
