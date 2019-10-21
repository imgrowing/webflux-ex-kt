package study.wf

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.Loggers
import reactor.util.function.component1
import reactor.util.function.component2

class SchedulerThreadTest {
    private val log = Loggers.getLogger(this.javaClass)

    private fun blockingMethod(duration: Long): Long {
        log.debug(">>>> blockingMethod(${duration} ms): start")
        Thread.sleep(duration)
        log.debug(">>>> blockingMethod(${duration} ms): end")
        return duration
    }

    @Test
    fun `wrappingBlockingMethod - fromCallable + subscribeOn`() {
        val scheduler = Schedulers.newParallel("asdf")// .newParallel("WRAPPING", 10)
        val block1 = Mono
                .fromCallable { blockingMethod(100) }
                .subscribeOn(scheduler)
        val block2 = Mono
                .fromCallable { blockingMethod(50) }
                .subscribeOn(scheduler)

        Mono
                .zip(block1, block2)
                .map { tuple ->
                    val (value1, value2) = tuple
                    val sum = value1 + value2
                    log.info("sum: $sum")
                    sum
                }
                .subscribe()

        Thread.sleep(300)
    }

    @Test
    fun `wrappingBlockingMethod - fromCallable + publishOn`() {
        val scheduler = Schedulers.newParallel("WRAPPING", 10)
        val block1 = Mono
                .fromCallable { blockingMethod(100) }
                .publishOn(scheduler)
        val block2 = Mono
                .fromCallable { blockingMethod(50) }
                .publishOn(scheduler)

        Mono
                .zip(block1, block2)
                .map { tuple ->
                    val (value1, value2) = tuple
                    val sum = value1 + value2
                    log.info("sum: $sum")
                    sum
                }
                .subscribe()

        Thread.sleep(300)
    }

    @Test
    fun `wrappingBlockingMethod - fromSupplier + subscribeOn`() {
        val scheduler = Schedulers.newParallel("WRAPPING", 10)
        val block1 = Mono
                .fromSupplier { blockingMethod(100) }
                .subscribeOn(scheduler)
        val block2 = Mono
                .fromSupplier { blockingMethod(50) }
                .subscribeOn(scheduler)

        Mono
                .zip(block1, block2)
                .map { tuple ->
                    val (value1, value2) = tuple
                    val sum = value1 + value2
                    log.info("sum: $sum")
                    sum
                }
                .subscribe()

        Thread.sleep(300)
    }

    @Test
    fun `wrappingBlockingMethod - fromSupplier + publishOn`() {
        val scheduler = Schedulers.newParallel("WRAPPING", 10)
        val block1 = Mono
                .fromSupplier { blockingMethod(100) }
                .publishOn(scheduler)
        val block2 = Mono
                .fromSupplier { blockingMethod(50) }
                .publishOn(scheduler)

        Mono
                .zip(block1, block2)
                .map { tuple ->
                    val (value1, value2) = tuple
                    val sum = value1 + value2
                    log.info("sum: $sum")
                    sum
                }
                .subscribe()

        Thread.sleep(300)
    }

}

