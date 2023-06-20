package datsok

import org.junit.Test
import java.util.Random

class LineByLineDiffTests {
    @Test fun `empty input has no diffs`() {
        compareStringsLineByLine(leftLines = emptyList(), rightLines = emptyList()) shouldEqual emptyList()
    }

    @Test fun `single line with no diffs`() {
        compareStringsLineByLine(leftLines = listOf("Hello"), rightLines = listOf("Hello")) shouldEqual emptyList()
    }

    @Test fun `multiple lines with no diffs`() {
        compareStringsLineByLine(
            leftLines = listOf("Line 1", "Line 2"),
            rightLines = listOf("Line 1", "Line 2")) shouldEqual emptyList()
    }

    @Test fun `missing lines on the left`() {
        val left = """
            Hello
            Goodbye
        """.trimIndent()
        val right = """
            Hello
            This is line 2
            This is line 3
            Goodbye
        """.trimIndent()
        compareStringsLineByLine(left.lines(), right.lines()) shouldEqual
            listOf(Difference(
                leftRange = 1..1,
                rightRange = 1..3,
                leftLines = emptyList(),
                rightLines = listOf("This is line 2", "This is line 3")
            ))
    }

    @Test fun `missing lines on the right`() {
        val left = """
            Hello
            This is line 2
            This is line 3
            Goodbye
        """.trimIndent()
        val right = """
            Hello
            Goodbye
        """.trimIndent()
        compareStringsLineByLine(left.lines(), right.lines()) shouldEqual
            listOf(Difference(
                leftRange = 1..3,
                rightRange = 1..1,
                leftLines = listOf("This is line 2", "This is line 3"),
                rightLines = emptyList()
            ))
    }

    @Test fun `different fragments with same amount of lines`() {
        val left = """
            Hello
            This is line 2
            This is line 3
            Goodbye
        """.trimIndent()
        val right = """
            Hello
            This is a different line 2
            This is a different line 3
            Goodbye
        """.trimIndent()
        compareStringsLineByLine(left.lines(), right.lines()) shouldEqual
            listOf(Difference(
                leftRange = 1..3,
                rightRange = 1..3,
                leftLines = listOf("This is line 2", "This is line 3"),
                rightLines = listOf("This is a different line 2", "This is a different line 3")
            ))
    }

    @Test fun `different fragments with more lines on the left`() {
        val left = """
            Hello
            This is line 2
            This is line 3
            Goodbye
        """.trimIndent()
        val right = """
            Hello
            This is a different line 2
            Goodbye
        """.trimIndent()
        compareStringsLineByLine(left.lines(), right.lines()) shouldEqual
            listOf(Difference(
                leftRange = 1..3,
                rightRange = 1..2,
                leftLines = listOf("This is line 2", "This is line 3"),
                rightLines = listOf("This is a different line 2")
            ))
    }

    @Test fun `different fragments with more lines on the right`() {
        val left = """
            Hello
            This is a different line 2
            Goodbye
        """.trimIndent()
        val right = """
            Hello
            This is line 2
            This is line 3
            Goodbye
        """.trimIndent()
        compareStringsLineByLine(left.lines(), right.lines()) shouldEqual
            listOf(Difference(
                leftRange = 1..2,
                rightRange = 1..3,
                leftLines = listOf("This is a different line 2"),
                rightLines = listOf("This is line 2", "This is line 3")
            ))
    }
}

class LongestCommonSubsequenceTests {
    @Test fun `empty input is empty subsequence`() {
        longestCommonSubsequence(leftLines = emptyList(), rightLines = emptyList()).toList() shouldEqual emptyList()
    }

    @Test fun `one matching line`() {
        longestCommonSubsequence(
            leftLines = listOf("Hello"),
            rightLines = listOf("Hello")
        ).toList() shouldEqual listOf(
            Match(left = 0, right = 0)
        )
    }

    @Test fun `matching lines`() {
        longestCommonSubsequence(
            leftLines = listOf("Line 1", "Line 2", "Line 3"),
            rightLines = listOf("Line 1", "Line 2", "Line 3")
        ).toList() shouldEqual listOf(
            Match(left = 0, right = 0),
            Match(left = 1, right = 1),
            Match(left = 2, right = 2)
        )
    }

    @Test fun `two matching and one different line`() {
        longestCommonSubsequence(
            leftLines = listOf("Line 1", "Line 2", "Line 3"),
            rightLines = listOf("Line 1", "Line 3")
        ).toList() shouldEqual listOf(
            Match(left = 0, right = 0),
            Match(left = 2, right = 1)
        )
    }

    @Test fun `no matching lines`() {
        longestCommonSubsequence(
            leftLines = listOf("Line 1", "Line 2", "Line 3"),
            rightLines = listOf("Line 4", "Line 5", "Line 6")
        ).toList() shouldEqual emptyList()
    }

    @Test fun `empty left input is empty subsequence`() {
        longestCommonSubsequence(
            leftLines = emptyList(),
            rightLines = listOf("Line 1", "Line 2", "Line 3")
        ).toList() shouldEqual emptyList()
    }

    @Test fun `empty right input is empty subsequence`() {
        longestCommonSubsequence(
            leftLines = listOf("Line 1", "Line 2", "Line 3"),
            rightLines = emptyList()
        ).toList() shouldEqual emptyList()
    }

    @Test fun `repeated line`() {
        longestCommonSubsequence(
            leftLines = listOf("Line 1", "Line 2", "Line 2", "Line 3"),
            rightLines = listOf("Line 1", "Line 2", "Line 3")
        ).toList() shouldEqual listOf(
            Match(left = 0, right = 0),
            Match(left = 1, right = 1),
            Match(left = 3, right = 2)
        )
    }

    @Test fun `lines in different order`() {
        longestCommonSubsequence(
            leftLines = listOf("Line 1", "Line 2", "Line 3"),
            rightLines = listOf("Line 3", "Line 2", "Line 1")
        ).toList() shouldEqual listOf(
            Match(left = 2, right = 0)
        )
    }

    @Test fun `one line is a subsequence of another`() {
        longestCommonSubsequence(
            leftLines = listOf("Line 1", "Line 2", "Line 3"),
            rightLines = listOf("Line 2")
        ).toList() shouldEqual listOf(
            Match(left = 1, right = 0)
        )
    }

    @Test fun `large input`() {
        val random = Random(123)
        longestCommonSubsequence(
            leftLines = List(size = 10_000) { "Line $it" }.shuffled(random),
            rightLines = List(size = 10_000) { "Line $it" }.shuffled(random)
        ).count()
    }
}
