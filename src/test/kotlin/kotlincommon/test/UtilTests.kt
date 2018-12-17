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