package by.overpas.detekt.intellij.jvm.general.apistatus

import org.jetbrains.annotations.ApiStatus

object ApiStatusOverrideOnlyLibrary {

    interface OverrideOnlyMethodInterface {
        @ApiStatus.OverrideOnly
        fun implementOnlyMethod()
    }

    open class OverrideOnlyMethodOwner {
        @ApiStatus.OverrideOnly
        open fun overrideOnlyMethod() =
            0

        open fun overrideOnlyMethod(value: Int) =
            value
    }

    @ApiStatus.OverrideOnly
    abstract class OverrideOnlyClass {
        abstract fun overrideOnlyMethod()

        fun finalMethod() =
            hashCode()

        companion object {
            @JvmStatic
            fun staticMethod() =
                hashCode()
        }
    }
}
