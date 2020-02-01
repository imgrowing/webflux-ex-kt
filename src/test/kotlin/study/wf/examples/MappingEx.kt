package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import study.wf.subscribeAndPrint
import java.time.Duration

class MappingEx {

    @Test
    fun `map operator`() {
        Flux
                .just(1, 2, 3, 4, 5)
                .map { it * it }
                .subscribeAndPrint()
    }

    @Test
    fun `index operator`() {
        Flux
                .just(2, 2, 3, 1)
                .index()
                .subscribeAndPrint()
        // Tuple<index, value>이 반환된다.
        // onNext -> [0,2]
        // onNext -> [1,2]
        // onNext -> [2,3]
        // onNext -> [3,1]
        // onComplete
    }

    @Test
    fun `timestamp operator`() {
        Flux
                .just(2, 2, 3, 1)
                .delayElements(Duration.ofMillis(10))
                .timestamp()
                .subscribeAndPrint()

        Thread.sleep(500)
        // onNext -> [1580539931041,2]
        // onNext -> [1580539931052,2]
        // onNext -> [1580539931063,3]
        // onNext -> [1580539931073,1]
        // onComplete
    }
}