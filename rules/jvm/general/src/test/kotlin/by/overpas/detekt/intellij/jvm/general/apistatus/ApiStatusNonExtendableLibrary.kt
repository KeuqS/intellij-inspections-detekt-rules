package by.overpas.detekt.intellij.jvm.general.apistatus

import org.jetbrains.annotations.ApiStatus

object ApiStatusNonExtendableLibrary {

    @ApiStatus.NonExtendable
    interface NonExtendableInterface

    @ApiStatus.NonExtendable
    open class NonExtendableClass

    open class NonExtendableMethodOwner {
        @ApiStatus.NonExtendable
        open fun doNotOverride() =
            0

        open fun mayOverride() =
            0
    }

    @ApiStatus.NonExtendable
    open class NonExtendableNestedClassOwner {
        open class NestedClass
    }

    open class NonExtendableNestedOwner {
        @ApiStatus.NonExtendable
        open class NonExtendableNested
    }
}
