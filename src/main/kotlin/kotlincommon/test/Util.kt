@file:Suppress("unused")

package kotlincommon.test


infix fun <T> T.shouldEqual(that: T) {
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

infix fun <T> T.shouldNotEqual(that: T) {
    if (this == that) throw AssertionError("Expected value not equal to: $that")
}

