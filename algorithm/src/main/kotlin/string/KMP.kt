package string

fun kmp(target: String, pattern: String): Int {
    val pmt = generatePMT(pattern)
    var j = 0
    for (i in target.indices) {
        if (j > 0 && target[i] != pattern[j]) {
            j = pmt[j - 1]
        }
        if (target[i] == pattern[j]) {
            j++
        }
        if (j == pattern.length) {
            return i - j + 1
        }
    }
    return -1
}

internal fun generatePMT(pattern: String): IntArray {
    val pmt = IntArray(pattern.length)

    var k = 0
    for (i in 1 until pattern.length) {
        while (k != 0 && pattern[k] != pattern[i]) {
            k = pmt[k]
        }
        if (pattern[k] == pattern[i]) {
            k++
        }
        pmt[i] = k
    }
    return pmt
}
