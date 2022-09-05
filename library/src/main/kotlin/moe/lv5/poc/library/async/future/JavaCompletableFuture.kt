package moe.lv5.poc.library.async.future

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

object JavaCompletableFuture {
    fun base(): CompletableFuture<Void> {
        val executor1 = Executors.newSingleThreadExecutor(ThreadFactory {
            return@ThreadFactory Thread(it, "Executor1").apply { isDaemon = true }
        })
        val executor2 = Executors.newSingleThreadExecutor(ThreadFactory {
            return@ThreadFactory Thread(it, "Executor2").apply { isDaemon = true }
        })
        val executor3 = Executors.newSingleThreadExecutor(ThreadFactory {
            return@ThreadFactory Thread(it, "Executor3").apply { isDaemon = true }
        })


        return CompletableFuture.supplyAsync(
            {
                println("first task: ${Thread.currentThread()}")
                Thread.sleep(1000)
                return@supplyAsync 1
            }, executor1
        ).thenApplyAsync(
            {
                println("second task: ${Thread.currentThread()}")
                println(it)
                return@thenApplyAsync 2
            }, executor2
        ).thenAcceptAsync(
            {
                println("last task: ${Thread.currentThread()}")
                println(it)
                throw IllegalCallerException("for test")
            }, executor3
        ).exceptionally {
            println("catch exception: " + it.message)
            throw it
            return@exceptionally null
        }
    }
}
