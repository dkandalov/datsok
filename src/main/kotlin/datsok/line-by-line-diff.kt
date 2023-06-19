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

internal fun longestCommonSubsequence_new(leftLines: List<String>, rightLines: List<String>): List<Match> {
    val (longList, shortList) = if (leftLines.size < rightLines.size) Pair(rightLines, leftLines) else Pair(leftLines, rightLines)
    var lengths = Array(2) { IntArray(shortList.size + 1) }

    val lcs = mutableListOf<Match>()

    for (i in longList.indices.reversed()) {
        val newLengths = Array(shortList.size + 1) { 0 }
        for (j in shortList.indices.reversed()) {
            if (longList[i] == shortList[j]) {
                newLengths[j] = lengths[1][j + 1] + 1
                lcs.add(Match(i, j))
            } else {
                newLengths[j] = maxOf(lengths[1][j], newLengths[j + 1])
            }
        }
        lengths = arrayOf(newLengths.toIntArray(), lengths[0])
    }

    return lcs.asReversed()
}


internal fun longestCommonSubsequence(leftLines: List<String>, rightLines: List<String>): List<Match> {
    val lengths = Array(leftLines.size + 1) { IntArray(rightLines.size + 1) }
    leftLines.indices.reversed().forEach { i ->
        rightLines.indices.reversed().forEach { j ->
            if (leftLines[i] == rightLines[j]) {
                lengths[i][j] = lengths[i + 1][j + 1] + 1
            } else {
                lengths[i][j] = maxOf(lengths[i + 1][j], lengths[i][j + 1])
            }
        }
    }

    var i = 0
    var j = 0
    val result = ArrayList<Match>()
    while (i < leftLines.size && j < rightLines.size) {
        when {
            leftLines[i] == rightLines[j] -> {
                result.add(Match(left = i, right = j))
                i++
                j++
            }
            lengths[i + 1][j] >= lengths[i][j + 1] -> i++
            else -> j++
        }
    }
    return result
}

internal data class Match(val left: Int, val right: Int)
