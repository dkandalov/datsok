@file:Suppress("unused")

package kotlincommon.test

import kotlincommon.test.FailedAssertionFinder.withAssertionError
import java.io.File
import java.io.IOException


infix fun <T> T.shouldEqual(that: T) = withAssertionError("shouldEqual") {
    if (this == that) return

    val (expectedPostfix, actualPostfix) =
        if (this != null && that != null && this.toString() == that.toString()) {
            // Cast to Any because of this issue https://youtrack.jetbrains.com/issue/KT-20778
            Pair(
                " (class " + (that as Any)::class.qualifiedName + ")",
                " (class " + (this as Any)::class.qualifiedName + ")"
            )
        } else {
            Pair("", "")
        }

    // Use this particular wording and indentation (which mostly replicates hamcrest)
    // so that IJ unit testing plugin can detect it and show <Click to see difference> link.
    // Note that `this` and `that` are printed on the same column on purpose so that it's easier to compare them visually.
    throw AssertionError(
        "\nExpected: $that$expectedPostfix" +
        "\n but: was $this$actualPostfix"
    )
}

infix fun <T> T.shouldNotEqual(that: T) = withAssertionError("shouldNotEqual") {
    if (this == that) throw AssertionError("Expected value not equal to: $that")
}


object FailedAssertionFinder {
    inline fun <T> withAssertionError(functionName: String, f: () -> T) {
        try {
            f()
        } catch (e: AssertionError) {
            val failedAssertion = findFailureFrame(e.stackTrace, "kotlincommon.test.UtilKt", functionName)?.readSourceCodeLine()
            if (failedAssertion != null) System.err.println("\nFailed at: $failedAssertion")
            throw e
        }
    }

    fun findFailureFrame(stackTrace: Array<StackTraceElement>, className: String, methodName: String): StackTraceElement? {
        for (i in stackTrace.indices) {
            val element = stackTrace[i]
            if (element.className == className && element.methodName == methodName)
                return stackTrace[i + 1]
        }
        return null
    }

    fun StackTraceElement.readSourceCodeLine(): String? =
        try {
            findSourceCodeFor(className)
                ?.readLines()
                ?.get(lineNumber - 1)
                ?.trim()
        } catch (ignored: IOException) {
            null
        }

    private fun findSourceCodeFor(className: String): File? {
        val paths1 = listOf("src", "")
        val paths2 = listOf("test", "main", "")
        val paths3 = listOf("kotlin", "java", "")
        val extensions = listOf(".kt", ".java")

        paths1.forEach { path1 ->
            paths2.forEach { path2 ->
                paths3.forEach { path3 ->
                    extensions.forEach { extension ->
                        val path = listOf(path1, path2, path3).joinToString("/").removePrefix("/")
                        val file = File(path + "/" + className.replace('.', '/') + extension)
                        if (file.exists()) return file
                    }
                }
            }
        }
        return null
    }
}
