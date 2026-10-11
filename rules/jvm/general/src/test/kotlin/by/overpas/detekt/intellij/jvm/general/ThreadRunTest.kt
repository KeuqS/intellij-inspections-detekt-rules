package by.overpas.detekt.intellij.jvm.general

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class ThreadRunTest {

    private val environment = createEnvironment()

    private val sut = ThreadRun(Config.empty)

    @Test
    fun `a run call on a thread is reported`() {
        val code = """
            class ThreadRunTest {
                fun doTest() {
                    val runnable = object : Runnable {
                        override fun run() {}
                    }
                    val thread = Thread(runnable)
                    thread.run()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a run call on a thread subclass is reported`() {
        val code = """
            class Worker : Thread() {
                override fun run() {}
            }

            fun test(worker: Worker) {
                worker.run()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a super run call passes`() {
        val code = """
            class ThreadRunSuperTest : Thread() {
                fun doTest() {
                    object : Thread("") {
                        override fun run() {
                            super.run()
                        }
                    }.start()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a run call on a runnable passes`() {
        val code = """
            fun test(runnable: Runnable) {
                runnable.run()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `the scope function run passes`() {
        val code = """
            fun test(thread: Thread) {
                thread.run { start() }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}
