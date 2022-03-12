package sort

private const val CUT_OFF = 15

fun <T : Comparable<T>> mergeSort(a: Array<T>) {
    val aux = a.clone()
    mergeSortInternally(aux, a, 0, a.size - 1)
}

private fun <T : Comparable<T>> mergeSortInternally(a: Array<T>, aux: Array<T>, low: Int, high: Int) {
    if (high - low <= CUT_OFF) {
        insertionSort(aux, low, high)
        return
    }
    val mid = low + (high - low) / 2
    mergeSortInternally(aux, a, low, mid)
    mergeSortInternally(aux, a, mid + 1, high)
    if (a[mid] > a[mid + 1]) {
        merge(a, aux, low, mid, high)
        return
    }
    System.arraycopy(a, low, aux, low, high - low + 1)
}

private fun <T : Comparable<T>> merge(a: Array<T>, aux: Array<T>, low: Int, mid: Int, high: Int) {
    var indexLeft = low
    var indexRight = mid + 1
    for (i in low..high) {
        when {
            indexLeft > mid -> aux[i] = a[indexRight++]
            indexRight > high -> aux[i] = a[indexLeft++]
            a[indexLeft] < a[indexRight] -> aux[i] = a[indexLeft++]
            else -> aux[i] = a[indexRight++]
        }
    }
}
