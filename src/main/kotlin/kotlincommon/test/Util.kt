@file:Suppress("unused")

package kotlincommon.test

import kotlincommon.test.FailedAssertionFinder.withAssertionError
import java.io.File
import java.io.IOException
import java.util.*


infix fun <T> T.shouldEqual(that: T): T = withAssertionError("shouldEqual") {
    if (this == that) return this
    if (this != null && that != null && areEqualArrays(this, that)) return this

    val (expectedPostfix, actualPostfix) =
        if (this != null && that != null && this.toPrintableString() == that.toPrintableString()) {
            // Cast to Any because of Kotlin/IJ issues,
            // see https://youtrack.jetbrains.com/issue/KT-20778, https://youtrack.jetbrains.com/issue/KT-39417
            @Suppress("USELESS_CAST")
            Pair(
                " (class " + (that as Any)::class.qualifiedName + ")",
                " (class " + (this as Any)::class.qualifiedName + ")"
            )
        } else {
            Pair("", "")
        }

    // Use this particular wording and indentation so that IJ shows <Click to see difference> link.
    // Based on regex in https://github.com/JetBrains/intellij-community/blob/master/plugins/junit_rt/src/com/intellij/junit4/ExpectedPatterns.java#L28
    // and using "Expected:... but was:..." format because IJ doesn't print failure twice for it.
    // (Also note that when running tests via Gradle, IJ won't show diff link, see https://youtrack.jetbrains.com/issue/IDEA-221624)
    throw AssertionError(
        "Expected: ${that.toPrintableString()}$expectedPostfix" +
        "\n but was: ${this.toPrintableString()}$actualPostfix"
    )
}

infix fun <T> T.shouldNotEqual(that: T) = withAssertionError("shouldNotEqual") {
    if (this == that) throw AssertionError("Expected value not equal to: $that")
}

private fun Any?.toPrintableString(): String =
    when (this) {
        is Array<*>     -> Arrays.toString(this)
        is BooleanArray -> Arrays.toString(this)
        is ByteArray    -> Arrays.toString(this)
        is CharArray    -> Arrays.toString(this)
        is ShortArray   -> Arrays.toString(this)
        is IntArray     -> Arrays.toString(this)
        is LongArray    -> Arrays.toString(this)
        is FloatArray   -> Arrays.toString(this)
        is DoubleArray  -> Arrays.toString(this)
        else            -> this.toString()
    }

private fun areEqualArrays(o1: Any, o2: Any): Boolean =
    when {
        o1 is Array<*> && o2 is Array<*>         -> o1.contentDeepEquals(o2)
        o1 is BooleanArray && o2 is BooleanArray -> o1.contentEquals(o2)
        o1 is ByteArray && o2 is ByteArray       -> o1.contentEquals(o2)
        o1 is CharArray && o2 is CharArray       -> o1.contentEquals(o2)
        o1 is ShortArray && o2 is ShortArray     -> o1.contentEquals(o2)
        o1 is IntArray && o2 is IntArray         -> o1.contentEquals(o2)
        o1 is LongArray && o2 is LongArray       -> o1.contentEquals(o2)
        o1 is FloatArray && o2 is FloatArray     -> o1.contentEquals(o2)
        o1 is DoubleArray && o2 is DoubleArray   -> o1.contentEquals(o2)
        else                                     -> false
    }


object FailedAssertionFinder {
    inline fun <T> withAssertionError(functionName: String, f: () -> T): T {
        try {
            return f()
        } catch (e: AssertionError) {
            val failedAssertion = findFailureFrame(e.stackTrace, "kotlincommon.test.UtilKt", functionName)?.readSourceCodeLine()
            if (failedAssertion != null) System.err.println("\nFailed at:\n$failedAssertion")
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
        val srcPaths = System.getProperty("kotlincommon.test.srcPaths", "").split(':')
        val extensions = listOf(".kt", ".java")

        (srcPaths + defaultSrcPaths()).forEach { path ->
            extensions.forEach { extension ->
                val file = File(path + "/" + className.replace('.', '/') + extension)
                if (file.exists()) return file
            }
        }
        return null
    }

    private fun defaultSrcPaths(): Sequence<String> = sequence {
        val paths1 = listOf("src", "")
        val paths2 = listOf("test", "main", "")
        val paths3 = listOf("kotlin", "java", "")
        paths1.forEach { path1 ->
            paths2.forEach { path2 ->
                paths3.forEach { path3 ->
                    yield(listOf(path1, path2, path3).joinToString("/").removePrefix("/"))
                }
            }
        }
    }
}
