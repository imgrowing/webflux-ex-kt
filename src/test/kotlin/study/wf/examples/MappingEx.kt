package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*

class MappingEx {

    @Test
    fun `map operator`() {
        Flux
                .just(1, 2, 3, 4, 5)
                .map { it * it }
                .subscribe(::println)
    }

    @Test
    fun `index operator`() {
        Flux
                .just(2, 2, 3, 1)
                .index()
                .subscribe { println("index: ${it.t1}, element: ${it.t2}") }
    }

    @Test
    fun `timestamp operator`() {
        Flux
                .just(2, 2, 3, 1)
                .delayElements(Duration.ofSeconds(1))
                .timestamp()
                .subscribe { println("${Date(it.t1)} : ${it.t1}, element: ${it.t2}") }

        Thread.sleep(4100)
    }
}