package datsok

fun compareStringsLineByLine(leftLines: List<String>, rightLines: List<String>): List<Difference> {
    var left = 0
    var right = 0
    val result = ArrayList<Difference>()
    longestCommonSubsequence(leftLines, rightLines).forEach { (leftMatch, rightMatch) ->
        if (left < leftMatch || right < rightMatch) {
            result.add(Difference(
                left..leftMatch,
                right..rightMatch,
                leftLines.subList(left, leftMatch),
                rightLines.subList(right, rightMatch)
            ))
        }
        left = leftMatch + 1
        right = rightMatch + 1
    }

    if (left < leftLines.size || right < rightLines.size) {
        result.add(Difference(
            left..leftLines.size,
            right..rightLines.size,
            leftLines.subList(left, leftLines.size),
            rightLines.subList(right, rightLines.size)
        ))
    }
    return result
}

data class Difference(
    val leftRange: IntRange,
    val rightRange: IntRange,
    val leftLines: List<String>,
    val rightLines: List<String>
)

internal fun longestCommonSubsequence(leftLines: List<String>, rightLines: List<String>): Sequence<Match> {
    val lengths = Array(leftLines.size + 1) { IntArray(rightLines.size + 1) }
    leftLines.indices.reversed().forEach { i ->
        rightLines.indices.reversed().forEach { j ->
            lengths[i][j] =
                if (leftLines[i] == rightLines[j]) lengths[i + 1][j + 1] + 1
                else maxOf(lengths[i + 1][j], lengths[i][j + 1])
        }
    }

    var i = 0
    var j = 0
    return sequence {
        while (i < leftLines.size && j < rightLines.size) {
            when {
                leftLines[i] == rightLines[j] -> {
                    yield(Match(left = i, right = j))
                    i++
                    j++
                }
                lengths[i + 1][j] >= lengths[i][j + 1] -> i++
                else -> j++
            }
        }
    }
}

internal data class Match(val left: Int, val right: Int)
