package by.overpas.detekt.intellij.jvm.general

import com.intellij.psi.PsiComment

private const val LINE_COMMENT_PREFIX = "//"

private const val SUPPRESS_TAG = "noinspection"

private const val VALID_ID = "[a-zA-Z_0-9.-]+"

private val SUPPRESS_COMMENT = Regex("""//\s*$SUPPRESS_TAG\s+($VALID_ID(\s*,\s*$VALID_ID)*)\s*\w*.*""")

internal fun PsiComment.suppressionCommentIds(): List<String>? {
    val isSuppressComment = text.length > LINE_COMMENT_PREFIX.length &&
        text.substring(LINE_COMMENT_PREFIX.length).trim().startsWith(SUPPRESS_TAG)
    val ids = SUPPRESS_COMMENT.matchEntire(text)
        ?.run { groupValues[1].split(",").map(String::trim).filter(String::isNotEmpty) }
        .orEmpty()
    return ids.takeIf { isSuppressComment }
}

internal fun List<String>.suppressionAnnotationList(): String {
    val quoted = map { "'$it'" }
    return when (quoted.size) {
        0, 1 -> quoted.joinToString()
        2 -> quoted.joinToString(" and ")
        else -> quoted.dropLast(1).joinToString(", ") + ", and " + quoted.last()
    }
}
