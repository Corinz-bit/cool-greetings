import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File

class PluginTest : FreeSpec({
    val pluginClasspathResource = ClassLoader.getSystemClassLoader().getResource("plugin-classpath.txt")
        ?: throw IllegalStateException("Did not find the plugin classpath descriptor.")
    val classpath = pluginClasspathResource.openStream().bufferedReader().use { reader ->
        reader.readLines().map { File(it) }
    }
    val greetTask = ":greetFakeName" // Colon needed!
    fun testSetup(buildFile: () -> String) = projectSetup(buildFile).let { testFolder ->
        GradleRunner.create()
            .withProjectDir(testFolder.root)
            .withPluginClasspath(classpath)
            .withArguments(greetTask)
    }
    "running the plugin with" - {
        "default configuration should" - {
            val runner = testSetup {
                """
                    plugins {
                        id("io.github.corinz-bit.greetings")
                    }
                """.trimIndent()
            }.build()
            "run the greet task" {
                runner.task(greetTask)?.outcome shouldBe TaskOutcome.SUCCESS
            }
            "print an hello message" {
                runner.output shouldContain "Hello from Gradle"
            }
        }
        "a message in Italian" - {
            val runner = testSetup {
                """
                    plugins {
                        id("io.github.corinz-bit.greetings")
                    }
                    greetings {
                        greetWith { "Ciao da" }
                    }
                """.trimIndent()
            }.build()
            "run the greet task" {
                runner.task(greetTask)?.outcome shouldBe TaskOutcome.SUCCESS
            }
            "print an hello message" {
                runner.output shouldContain "Ciao da Gradle"
            }
        }
    }
})

fun projectSetup(content: String) = TemporaryFolder().apply{
    create()
    newFile("build.gradle.kts").writeText(content)
}

fun projectSetup(content: () -> String) = projectSetup(content())

