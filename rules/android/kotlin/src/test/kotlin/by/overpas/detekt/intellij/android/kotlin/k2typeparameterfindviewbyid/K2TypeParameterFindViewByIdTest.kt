package by.overpas.detekt.intellij.android.kotlin.k2typeparameterfindviewbyid

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class K2TypeParameterFindViewByIdTest {

    private val environment = createEnvironment()

    private val sut = K2TypeParameterFindViewById(Config.empty)

    @Test
    fun `a cast of a findViewById call is reported`() {
        val code = """
            import android.app.Activity
            import android.widget.TextView

            fun show(activity: Activity) {
                val text = activity.findViewById(0) as TextView
            }
        """.trimIndent()
        val app = """
            package android.app

            import android.view.View

            open class Activity {
                fun <T : View> findViewById(id: Int): T = throw IllegalStateException()
            }
        """.trimIndent()
        val view = """
            package android.view

            open class View
        """.trimIndent()
        val widget = """
            package android.widget

            import android.view.View

            class TextView : View()
        """.trimIndent()
        val expected = listOf("activity.findViewById(0) as TextView")

        val findings = sut.lintWithContext(environment, code, app, view, widget)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a cast to a nullable type is reported`() {
        val code = """
            package android.app

            import android.widget.Button

            class MainActivity : Activity() {
                fun show() {
                    val button = findViewById(0) as Button?
                }
            }
        """.trimIndent()
        val app = """
            package android.app

            import android.view.View

            open class Activity {
                fun <T : View> findViewById(id: Int): T? = null
            }
        """.trimIndent()
        val view = """
            package android.view

            open class View
        """.trimIndent()
        val widget = """
            package android.widget

            import android.view.View

            class Button : View()
        """.trimIndent()
        val expected = listOf("findViewById(0) as Button?")

        val findings = sut.lintWithContext(environment, code, app, view, widget)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a cast of a findViewWithTag call is reported`() {
        val code = """
            import android.view.View
            import android.widget.TextView

            fun show(root: View) {
                val text = root.findViewWithTag("tag") as TextView
            }
        """.trimIndent()
        val view = """
            package android.view

            open class View {
                fun <T : View> findViewWithTag(tag: Any): T = throw IllegalStateException()
            }
        """.trimIndent()
        val widget = """
            package android.widget

            import android.view.View

            class TextView : View()
        """.trimIndent()
        val expected = listOf("root.findViewWithTag(\"tag\") as TextView")

        val findings = sut.lintWithContext(environment, code, view, widget)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a safe qualified requireViewById call in parentheses is reported`() {
        val code = """
            import android.view.View
            import android.widget.TextView

            fun show(root: View?) {
                val text = (root?.requireViewById(0)) as TextView?
            }
        """.trimIndent()
        val view = """
            package android.view

            open class View {
                fun <T : View> requireViewById(id: Int): T = throw IllegalStateException()
            }
        """.trimIndent()
        val widget = """
            package android.widget

            import android.view.View

            class TextView : View()
        """.trimIndent()
        val expected = listOf("(root?.requireViewById(0)) as TextView?")

        val findings = sut.lintWithContext(environment, code, view, widget)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a user function with the same name and a view type parameter is reported`() {
        val code = """
            class Screen {
                fun <T : View> requireViewById(id: Int): T = throw IllegalStateException()
            }

            open class View

            class TextView : View()

            val text = Screen().requireViewById(0) as TextView
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a function that returns a definitely non nullable type parameter is reported`() {
        val code = """
            fun <T : View?> requireViewById(id: Int): T & Any = throw IllegalStateException()

            open class View

            class TextView : View()

            val text = requireViewById(0) as TextView
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a call with a type argument passes`() {
        val code = """
            fun <T : View> findViewById(id: Int): T? = null

            open class View

            class TextView : View()

            class Button : View()

            val text = findViewById<Button>(0) as TextView
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a safe cast passes`() {
        val code = """
            fun <T : View> findViewById(id: Int): T? = null

            open class View

            class TextView : View()

            val text = findViewById(0) as? TextView
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a call without a cast passes`() {
        val code = """
            fun <T : View> findViewById(id: Int): T? = null

            open class View

            class TextView : View()

            fun show(text: TextView?) {}

            fun main() {
                show(findViewById(0))
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a type check passes`() {
        val code = """
            fun <T : View> findViewById(id: Int): T? = null

            open class View

            class TextView : View()

            val isText = findViewById<View>(0) is TextView
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a cast target outside the type parameter bounds passes`() {
        val code = """
            fun <T : View> requireViewById(id: Int): T = throw IllegalStateException()

            open class View

            class Bundle

            val bundle = requireViewById(0) as Bundle
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function without a type parameter passes`() {
        val code = """
            fun findViewById(id: Int): View = View()

            open class View

            class TextView : View()

            val text = findViewById(0) as TextView
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function that does not return its type parameter passes`() {
        val code = """
            fun <T> findViewById(id: T): View = View()

            open class View

            class TextView : View()

            val text = findViewById(0) as TextView
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function with two type parameters passes`() {
        val code = """
            fun <T, U> findViewById(t: T, u: U): View = View()

            open class View

            class TextView : View()

            val text = findViewById(0, 1) as TextView
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function with another name passes`() {
        val code = """
            fun <T : View> findView(id: Int): T? = null

            open class View

            class TextView : View()

            val text = findView(0) as TextView
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}
