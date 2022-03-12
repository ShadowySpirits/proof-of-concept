package sort

private const val CUT_OFF = 15

fun <T : Comparable<T>> quickSort(a: Array<T>) {
    quickSortInternally(a, 0, a.size - 1)
}

private fun <T : Comparable<T>> quickSortInternally(a: Array<T>, low: Int, high: Int) {
    if (high - low <= CUT_OFF) {
        insertionSort(a, low, high)
        return
    }
    val pivot = partition(a, low, high)
    quickSortInternally(a, low, pivot - 1)
    quickSortInternally(a, pivot + 1, high)
}

private fun <T : Comparable<T>> partition(a: Array<T>, low: Int, high: Int): Int {
    val mid = (low + high) / 2
    if (a[mid] < a[low]) {
        exchange(a, mid, low)
    }
    if (a[high] < a[mid]) {
        exchange(a, mid, high)
    }
    if (a[high] < a[low]) {
        exchange(a, high, low)
    }
    exchange(a, mid, high)
    var i = low
    for (j in low until high) {
        if (a[j] < a[high]) {
            exchange(a, i, j)
            i++
        }
    }
    exchange(a, i, high)
    return i
}

private fun <T : Comparable<T>> exchange(a: Array<T>, i: Int, j: Int) {
    val t = a[i]
    a[i] = a[j]
    a[j] = t
}
