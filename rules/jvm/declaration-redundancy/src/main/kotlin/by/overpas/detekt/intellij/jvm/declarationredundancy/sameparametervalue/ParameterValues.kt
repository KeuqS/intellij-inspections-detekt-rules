package by.overpas.detekt.intellij.jvm.declarationredundancy.sameparametervalue

internal class ParameterValues(count: Int) {

    private val values = MutableList<ArgumentValue?>(count) { null }

    val presentations: List<String?>
        get() = values.map { it?.presentation() }

    fun add(arguments: List<ArgumentValue>) =
        arguments.forEachIndexed { index, argument ->
            val current = values[index]
            values[index] = if (current == null || current == argument) argument else ArgumentValue.NotConstant
        }
}
