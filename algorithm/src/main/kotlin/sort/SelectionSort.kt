package sort

fun <T : Comparable<T>> selectionSort(a: Array<T>) {
    if (a.size <= 1) return
    for (exchangeBorder in a.indices) {
        var minIndex = exchangeBorder
        for (i in exchangeBorder + 1 until a.size) {
            if (a[i] < a[minIndex]) {
                minIndex = i
            }
        }
        val t = a[exchangeBorder]
        a[exchangeBorder] = a[minIndex]
        a[minIndex] = t
    }
}
