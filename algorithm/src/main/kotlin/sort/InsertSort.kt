package sort

fun <T : Comparable<T>> insertionSort(a: Array<T>) {
    if (a.size <= 1) return
    for (i in a.indices) {
        val value = a[i]
        var j = i - 1
        while (j >= 0 && a[j] > value) {
            a[j + 1] = a[j]
            --j
        }
        a[j + 1] = value
    }
}

fun <T : Comparable<T>> insertionSort(a: Array<T>, low: Int, high: Int) {
    if (high - low + 1 <= 1) return
    for (i in low..high) {
        val value = a[i]
        var j = i - 1
        while (j >= low && a[j] > value) {
            a[j + 1] = a[j]
            --j
        }
        a[j + 1] = value
    }
}

fun <T : Comparable<T>> shellSort(a: Array<T>) {
    if (a.size <= 1) return
    var gap = a.size / 2
    while (gap >= 1) {
        for (i in gap until a.size) {
            val value = a[i]
            var j = i - gap
            while (j >= 0 && a[j] > value) {
                a[j + gap] = a[j]
                j -= gap
            }
            a[j + gap] = value
        }
        gap /= 2
    }
}
