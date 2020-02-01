package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import study.wf.subscribeAndPrint
import java.time.Duration.ofMillis

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
                .delayElements(ofMillis(10))
                .timestamp()
                .subscribeAndPrint()

        Thread.sleep(500)
        // onNext -> [1580539931041,2]
        // onNext -> [1580539931052,2]
        // onNext -> [1580539931063,3]
        // onNext -> [1580539931073,1]
        // onComplete
    }

    @Test
    fun `flatMap - map 후 flatten, 내부 스트림을 동시에 병렬로 구독하여 하나의 스트림으로 합친다, 내부 스트림 간의 순서가 유지되지 않는다`() {
        Flux
                .just(0, 1, 2)
                .flatMap {
                    Flux
                            .range(it * 3, 3)      // Flux<Int 3개 짜리>
                            .delayElements(ofMillis(10))
                }
                .subscribeAndPrint()
        Thread.sleep(120)
        // onNext -> 0
        // onNext -> 3
        // onNext -> 6
        // onNext -> 4
        // onNext -> 1
        // onNext -> 7
        // onNext -> 5
        // onNext -> 2
        // onNext -> 8
        // onComplete
    }

    @Test
    fun `concatMap - map 후 concat, 내부 스트림을 순차적으로 하나씩 구독하여 하나의 스트림으로 합친다, 내부 스트림 간의 순서가 유지된다`() {
        Flux
                .just(0, 1, 2)
                .concatMap {
                    Flux
                            .range(it * 3, 3)      // Flux<Int 3개 짜리>
                            .delayElements(ofMillis(10))
                }
                .subscribeAndPrint()
        Thread.sleep(120)
        // onNext -> 0
        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onNext -> 4
        // onNext -> 5
        // onNext -> 6
        // onNext -> 7
        // onNext -> 8
        // onComplete
    }

    @Test
    fun `flatMapSequential - map 후 flatten, 내부 스트림을 동시에 병렬로 구독하지만 각각의 큐에 넣어 최종적으로 하나의 스트림으로 합친다, 내부 스트림 간의 순서가 유지된다`() {
        Flux
                .just(0, 1, 2)
                .flatMapSequential {
                    Flux
                            .range(it * 3, 3)      // Flux<Int 3개 짜리>
                            .delayElements(ofMillis(10))
                }
                .subscribeAndPrint()
        Thread.sleep(120)
        // onNext -> 0
        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onNext -> 4
        // onNext -> 5
        // onNext -> 6
        // onNext -> 7
        // onNext -> 8
        // onComplete
    }
}