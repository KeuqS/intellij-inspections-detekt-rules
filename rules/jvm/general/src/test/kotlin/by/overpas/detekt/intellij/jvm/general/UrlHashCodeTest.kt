package by.overpas.detekt.intellij.jvm.general

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlHashCodeTest {

    private val environment = createEnvironment()

    private val sut = UrlHashCode(Config.empty)

    @Test
    fun `a hashCode call on a url is reported`() {
        val code = """
            import java.net.URL

            fun foo() {
                val url = URL("")
                url.hashCode()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an equality check and an equals call on urls are reported`() {
        val code = """
            import java.net.URL

            fun foo() {
                val url1 = URL("")
                val url2 = URL("")
                url1 == url2
                url1.equals(url2)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `variables of url maps and sets are reported`() {
        val code = """
            import java.net.URL

            class CollectionContainsUrl {
                val strMap: Map<String, String> = HashMap()
                val urlMap: Map<URL, String> = HashMap()
                val urlMapOfMap: Map<String, Map<URL, String>> = HashMap()
                val urlSet: Set<URL> = HashSet()
                val urlSetOfMap: Set<Map<URL, String>> = HashSet()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(4, findings.size)
    }

    @Test
    fun `a parameter of a url set is reported`() {
        val code = """
            import java.net.URL

            fun foo(urls: MutableSet<URL>) = urls.size
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a url map operation is reported at the receiver`() {
        val code = """
            import java.net.URL

            class CollectionContainsUrl {
                val objMap: MutableMap<Any, Any> = HashMap()

                fun foo() {
                    objMap.put(URL(""), "")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a url set operation is reported at the receiver`() {
        val code = """
            import java.net.URL

            class CollectionContainsUrl {
                val objSet: MutableSet<Any> = HashSet()

                fun foo() {
                    objSet.add(URL(""))
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a url operation on a java hash set is reported`() {
        val code = """
            import java.net.URL

            fun foo(urls: java.util.HashSet<Any>) {
                urls.contains(URL(""))
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a url operation without a receiver is reported`() {
        val code = """
            import java.net.URL

            class Urls : HashSet<Any>() {
                fun foo() {
                    add(URL(""))
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `url method references are reported`() {
        val code = """
            import java.net.URL

            fun foo(url: URL) {
                val hash = url::hashCode
                val equality = URL::equals
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `comparisons with null pass`() {
        val code = """
            fun main() {
                val sample = java.net.URL("")
                if (sample == null) {}
                if (null == sample) {}
                sample.equals(null)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `uri usage passes`() {
        val code = """
            import java.net.URI

            fun foo(uri: URI, uris: Set<URI>) {
                uri.hashCode()
                uri == URI("")
                uris.contains(uri)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an inequality check passes`() {
        val code = """
            import java.net.URL

            fun foo(url1: URL, url2: URL) = url1 != url2
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}
