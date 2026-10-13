package by.overpas.detekt.intellij.kotlin.styleissues.destructuringdeclaration

private const val DESTRUCTURING_DECLARATION_MAX_UNUSED_COMPONENTS = 2

private const val DESTRUCTURING_DECLARATION_MIN_USED_COMPONENTS = 2

internal class DestructuringDeclarationUsages(usages: List<DestructuringDeclarationUsage>) {

    private val claims = usages.map { it.claimedIndices() }

    private val destructured = usages.filter { it.isDestructuring() }.flatMap { it.claimedIndices().orEmpty() }

    private val dropped = usages.filter { it.dropsStatement() && !it.isDestructuring() }
        .flatMap { it.claimedIndices().orEmpty() }

    fun isDestructurable(maxEntries: Int): Boolean {
        val used = claims.filterNotNull().flatten().toSet()
        val entries = (used.maxOrNull() ?: -1) + 1
        val unused = (0..<entries).count { it !in used }
        return claims.none { it == null } &&
            destructured.size == destructured.toSet().size &&
            destructured.none { it in dropped } &&
            used.size >= DESTRUCTURING_DECLARATION_MIN_USED_COMPONENTS &&
            unused <= DESTRUCTURING_DECLARATION_MAX_UNUSED_COMPONENTS &&
            entries <= maxEntries
    }
}
