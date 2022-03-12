package string

import kotlin.math.max

private const val SIZE = 256

fun bm(target: String, pattern: String): Int {
    val bc = generateBC(pattern)
    val (suffix, prefix) = generateGS(pattern)
    var i = 0
    while (i <= target.length - pattern.length) {
        var j = pattern.length - 1
        while (j >= 0 && pattern[j] == target[i + j]) {
            j--
        }
        if (j < 0) {
            return i
        }
        val bcShift = j - bc[target[i + j].code]
        val k = pattern.length - 1 - j
        var gsShift = 0
        if (j < pattern.length - 1) {
            if (suffix[k] != -1) {
                gsShift = j - suffix[k] + 1
            } else {
                gsShift = pattern.length
                for (r in j + 2 until pattern.length) {
                    if (prefix[pattern.length - r]) {
                        gsShift = r
                    }
                }
            }
        }
        i += max(bcShift, gsShift)
    }
    return -1
}

internal fun generateBC(pattern: String): IntArray {
    val bc = IntArray(SIZE) { return@IntArray -1 }
    for ((i, s) in pattern.withIndex()) {
        bc[s.code] = i
    }
    return bc
}

internal fun generateGS(pattern: String): Pair<IntArray, BooleanArray> {
    val suffix = IntArray(pattern.length) { return@IntArray -1 }
    val prefix = BooleanArray(pattern.length) { return@BooleanArray false }
    val m = pattern.length - 1
    for (i in 0 until m) {
        var j = i
        var k = 0
        while (j >= 0 && pattern[j] == pattern[m - k]) {
            suffix[++k] = j--
        }
        if (j < 0) {
            prefix[k] = true
        }
    }
    return Pair(suffix, prefix)
}
