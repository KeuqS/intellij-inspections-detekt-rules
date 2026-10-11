package by.overpas.detekt.intellij.jvm.general.apistatus

import org.jetbrains.annotations.ApiStatus

object ApiStatusUnstableLibrary {

    @ApiStatus.Experimental
    open class ExperimentalClass @ApiStatus.Experimental constructor(val value: String) {
        val nonAnnotatedField = value

        @ApiStatus.Experimental
        val annotatedField = value

        constructor() : this("")

        open fun nonAnnotatedMethod() =
            0

        @ApiStatus.Experimental
        open fun annotatedMethod() =
            0
    }

    open class StableClass @ApiStatus.Experimental constructor(val value: String) {
        val nonAnnotatedField = value

        @ApiStatus.Experimental
        val annotatedField = value

        constructor() : this("")

        open fun nonAnnotatedMethod() =
            0

        @ApiStatus.Experimental
        open fun annotatedMethod() =
            0
    }

    @ApiStatus.Experimental
    enum class ExperimentalEnum { VALUE, }

    enum class StableEnum {
        NON_ANNOTATED_VALUE,

        @ApiStatus.Experimental
        ANNOTATED_VALUE,
    }

    class ExperimentalSignatureOwner {
        val field: ExperimentalClass? = null

        fun parameterType(param: ExperimentalClass?) =
            param

        fun returnType(): List<ExperimentalClass> =
            emptyList()
    }

    open class ClassWithExperimentalTypeInSignature<T : ExperimentalClass>

    @Deprecated("Use StableClass")
    @ApiStatus.ScheduledForRemoval(inVersion = "123.456")
    open class ScheduledForRemovalClass {
        fun method() =
            hashCode()
    }

    @Deprecated("Use StableClass")
    @ApiStatus.ScheduledForRemoval
    class ScheduledForRemovalWithoutVersion
}
