class Watcher {

    private var memoryBefore: Long = 0
    private var timeBefore: Long = 0

    init {
        setUp()
    }

    fun setUp() {
        System.gc()
        memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        timeBefore = System.currentTimeMillis()
    }

    fun elapsedTime(): Long {
        return System.currentTimeMillis() - timeBefore
    }

    fun usedMemory(): Double {
        val usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576.0 -
                memoryBefore / 1048576.0
        System.gc()
        return usedMemory
    }
}
