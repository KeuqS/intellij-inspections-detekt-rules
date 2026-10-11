package by.overpas.detekt.intellij.jvm.logging

internal class LevelNames(private val levels: Set<String>) {

    fun byName(name: String): String? {
        val upperName = name.uppercase()
        return upperName.takeIf { it in levels } ?: upperName.removePrefix("AT").takeIf { it in levels }
    }

    fun byGuard(methodName: String): String? {
        if (!methodName.startsWith("is") || !methodName.endsWith("Enabled") || methodName.length < GUARD_AFFIXES) {
            return null
        }
        return methodName.substring(2, methodName.length - "Enabled".length).uppercase().takeIf { it in levels }
    }

    companion object {
        private const val GUARD_AFFIXES = 9
        val ACTUAL = LevelNames(LevelType.entries.map { it.name }.toSet())
        val LEGACY = LevelNames(LegacyLevelType.entries.map { it.name }.toSet())
    }
}
