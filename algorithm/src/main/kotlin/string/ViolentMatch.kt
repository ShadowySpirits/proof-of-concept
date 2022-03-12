package string

fun violentMatch(target: String, pattern: String): Int {
    var i = 0
    var j: Int
    while (i < target.length) {
        j = 0
        while (j < pattern.length && target[i] == pattern[j]) {
            i++
            j++
        }
        if (j == pattern.length) {
            return i - j
        }
        i = i - j + 1
    }
    return -1
}
