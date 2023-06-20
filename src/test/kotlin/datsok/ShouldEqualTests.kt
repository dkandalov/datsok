package datsok

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.fail
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals


class ShouldEqualTests {
    @Test fun `passing assertions`() {
//        throw AssertionError("foo")
//        assertEquals("fixme", "bar")

//        123 shouldEqual 111
        123 shouldNotEqual 42

        "foo" shouldEqual "foo"
        "foo" shouldNotEqual "bar"

        "foo" shouldNotEqual 123
        123 shouldNotEqual "foo"
        null shouldEqual null

        booleanArrayOf(true) shouldEqual booleanArrayOf(true)
        byteArrayOf(1) shouldEqual byteArrayOf(1)
        charArrayOf('a') shouldEqual charArrayOf('a')
        shortArrayOf(1) shouldEqual shortArrayOf(1)
        intArrayOf(1) shouldEqual intArrayOf(1)
        longArrayOf(1L) shouldEqual longArrayOf(1L)
        floatArrayOf(1.0f) shouldEqual floatArrayOf(1.0f)
        doubleArrayOf(1.0) shouldEqual doubleArrayOf(1.0)

        emptyArray<Int>() shouldEqual emptyArray()
        arrayOf(1, 2, 3) shouldEqual arrayOf(1, 2, 3)
        arrayOf(1, 2, "foo") shouldEqual arrayOf(1, 2, "foo")
        arrayOfNulls<Int>(size = 1) shouldEqual arrayOfNulls(size = 1)

        (1 + 2 shouldEqual 3) * 5 shouldEqual 15
    }

    @Test fun `failing assertions`() {
        expectAssertionError({ 1 shouldEqual 2 }, """
            |Expected: 2
            | but was: 1
        """.trimMargin())

        expectAssertionError({ 1 shouldEqual 1L }, """
            |Expected: 1 (class kotlin.Long)
            | but was: 1 (class kotlin.Int)
        """.trimMargin())

        expectAssertionError({ "1" shouldEqual 1 }, """
            |Expected: 1 (class kotlin.Int)
            | but was: 1 (class kotlin.String)
        """.trimMargin())

        expectAssertionError({ "1" shouldEqual "1 " }, """
            |Expected: 1 
            | but was: 1
            |(different because of prefix/postfix whitespaces)
        """.trimMargin())

        expectAssertionError({ "1\n" shouldEqual "1" }, """
            |Expected: 1
            | but was: 1
            |
            |(different because of prefix/postfix whitespaces)
        """.trimMargin())

        expectAssertionError({ 1 shouldNotEqual 1 }, """
            Expected value not equal to: 1
            """.trimIndent()
        )

        expectAssertionError({ arrayOf(1) shouldEqual arrayOf(1, 2) }, """
            |Expected: [1, 2]
            | but was: [1]
            """.trimMargin()
        )
        expectAssertionError({ intArrayOf(1) shouldEqual intArrayOf(1, 2) }, """
            |Expected: [1, 2]
            | but was: [1]
            """.trimMargin()
        )
        expectAssertionError({ intArrayOf(1, 2) shouldEqual arrayOf(1, 2) }, """
            |Expected: [1, 2] (class kotlin.Array)
            | but was: [1, 2] (class kotlin.IntArray)
            """.trimMargin()
        )
        expectAssertionError({ arrayOfNulls<Int>(size = 1) shouldEqual arrayOfNulls(size = 3) }, """
            |Expected: [null, null, null]
            | but was: [null]
            """.trimMargin()
        )
    }
}

class ShouldThrowTests {
    @Test fun `passing assertions`() {
        shouldThrow<IllegalStateException> { throw IllegalStateException() }
        shouldThrow<RuntimeException> { throw IllegalStateException() }
        shouldThrow<Exception> { throw IllegalStateException() }

        shouldThrow(SomeException("a message")) { throw SomeException("a message") }

        // Shouldn't compile
        // shouldThrow<Throwable> { throw IllegalStateException() }
        // shouldThrow<Error> { throw InternalError() }
    }

    @Test fun `failing assertions`() {
        expectAssertionError(
            action = { shouldThrow<IllegalStateException> { } },
            expectedMessage = "Expected exception java.lang.IllegalStateException"
        )
        expectAssertionError(
            action = { shouldThrow<IllegalStateException> { throw NullPointerException() } },
            expectedMessage = "Expected exception java.lang.IllegalStateException but was java.lang.NullPointerException"
        )

        expectAssertionError(
            action = { shouldThrow(SomeException("foo")) { throw SomeException("bar") } },
            expectedMessage = "Expected exception SomeException(message=foo) but was SomeException(message=bar)"
        )
        expectAssertionError(
            action = { shouldThrow(SomeException("foo")) { throw NullPointerException() } },
            expectedMessage = "Expected exception SomeException(message=foo) but was java.lang.NullPointerException"
        )
    }

    private data class SomeException(override val message: String) : Exception()
}

private fun expectAssertionError(action: () -> Unit, expectedMessage: String) {
    try {
        action()
        fail("Expected failure")
    } catch (e: AssertionError) {
        assertThat(e.message, equalTo(expectedMessage))
    }
}
