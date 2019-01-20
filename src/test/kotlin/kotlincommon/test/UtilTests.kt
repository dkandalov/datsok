package kotlincommon.test

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.fail
import org.junit.Test

class UtilTests {

    @Test fun `passing assertions`() {
        1 shouldEqual 1
        1 shouldNotEqual 2

        "abc" shouldEqual "abc"
        "abc" shouldNotEqual "bcd"

        "abc" shouldNotEqual 123
        123 shouldNotEqual "abc"

        booleanArrayOf(true) shouldEqual booleanArrayOf(true)
        byteArrayOf(1) shouldEqual byteArrayOf(1)
        charArrayOf('a') shouldEqual charArrayOf('a')
        shortArrayOf(1) shouldEqual shortArrayOf(1)
        intArrayOf(1) shouldEqual intArrayOf(1)
        longArrayOf(1L) shouldEqual longArrayOf(1L)
        floatArrayOf(1.0f) shouldEqual floatArrayOf(1.0f)
        doubleArrayOf(1.0) shouldEqual doubleArrayOf(1.0)

        emptyArray<Int>() shouldEqual emptyArray()
        arrayOf(1) shouldEqual arrayOf(1)
        arrayOf(1, 2, "foo") shouldEqual arrayOf(1, 2, "foo")
        arrayOfNulls<Int>(size = 1) shouldEqual arrayOfNulls(size = 1)
    }

    @Test fun `failing assertions`() {
        expectFailure({ 1 shouldEqual 2 }, """
            |
            |Expected: 2
            | but: was 1
        """.trimMargin())

        expectFailure({ 1 shouldEqual 1L }, """
            |
            |Expected: 1 (class kotlin.Long)
            | but: was 1 (class kotlin.Int)
        """.trimMargin())

        expectFailure({ "1" shouldEqual 1 }, """
            |
            |Expected: 1 (class kotlin.Int)
            | but: was 1 (class kotlin.String)
        """.trimMargin())

        expectFailure({ 1 shouldNotEqual 1 }, """
            Expected value not equal to: 1
            """.trimIndent()
        )

        expectFailure({ arrayOf(1) shouldEqual arrayOf(1, 2) }, """
            |
            |Expected: [1, 2]
            | but: was [1]
            """.trimMargin()
        )
        expectFailure({ intArrayOf(1) shouldEqual intArrayOf(1, 2) }, """
            |
            |Expected: [1, 2]
            | but: was [1]
            """.trimMargin()
        )
        expectFailure({ intArrayOf(1, 2) shouldEqual arrayOf(1, 2) }, """
            |
            |Expected: [1, 2] (class kotlin.Array)
            | but: was [1, 2] (class kotlin.IntArray)
            """.trimMargin()
        )
        expectFailure({ arrayOfNulls<Int>(size = 1) shouldEqual arrayOfNulls(size = 3) }, """
            |
            |Expected: [null, null, null]
            | but: was [null]
            """.trimMargin()
        )
    }

    private fun expectFailure(failingTest: () -> Unit, expectedMessage: String) {
        try {
            failingTest()
            fail("Expected failure")
        } catch (e: AssertionError) {
            assertThat(e.message, equalTo(expectedMessage))
        }
    }
}