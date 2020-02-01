package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import study.wf.subscribeAndPrint
import java.time.Duration

class WindowBufferEx {

    @Test
    fun `buffer, no arg - 스트림을 하나의 List로 내보낸다`() {
        Flux
                .range(1, 10)
                .buffer()  // Flux<List<T>>
                .subscribeAndPrint()
        // onNext -> [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        // onComplete
    }

    @Test
    fun `buffer, chunkSize - 스트림을 chunk size 크기만큼의 List로 분할하여 내보낸다`() {
        Flux
                .range(1, 10)
                .buffer(6)
                .subscribeAndPrint()
        // onNext -> [1, 2, 3, 4, 5, 6]
        // onNext -> [7, 8, 9, 10]
        // onComplete
    }

    @Test
    fun `buffer, duration - 스트림을 duration 기간 만큼의 List로 분할하여 내보낸다`() {
        Flux
                .range(1, 10)
                .delayElements(Duration.ofMillis(20))
                .buffer(Duration.ofMillis(100))
                .subscribeAndPrint()
        Thread.sleep(300)
        // onNext -> [1, 2, 3]
        // onNext -> [4, 5, 6, 7]
        // onNext -> [8, 9, 10]
        // onComplete
    }

    @Test
    fun `window, chunkSize - 스트림을 chunk size 크기만큼의 스트림으로 분할하여 내보낸다`() {
        Flux
                .range(1, 10)
                .window(6)  // Flux<Flux<T>>
                .subscribeAndPrint()
        // onNext -> 1, 2, 3, 4, 5, 6, -> onComplete
        // onNext -> 7, 8, 9, 10, -> onComplete
        // onComplete
    }

    @Test
    fun `window, duration - 스트림을 duration 기간 만큼의 스트림으로 분할하여 내보낸다`() {
        Flux
                .range(1, 10)
                .delayElements(Duration.ofMillis(20))
                .window(Duration.ofMillis(100))
                .subscribeAndPrint()
        Thread.sleep(300)
        // onNext -> 1, 2, -> onComplete
        // onNext -> 3, 4, 5, 6, -> onComplete
        // onNext -> 7, 8, 9, 10, -> onComplete
        // onComplete
    }

    @Test
    fun `window, until - 스트림을 특정 조건을 만족할 때 스트림으로 분할하여 내보낸다`() {
        Flux
                .range(1, 10)
                .windowUntil { it % 3 == 0 }
                .subscribeAndPrint()
        // onNext -> [1, 2, 3]
        // onNext -> [4, 5, 6]
        // onNext -> [7, 8, 9]
        // onNext -> [10]
        // onComplete
    }

    @Test
    fun `window, until, cutBefore - 스트림을 특정 조건을 만족할 때 스트림으로 분할하여 내보낸다`() {
        Flux
                .range(1, 10)
                .windowUntil({ it % 3 == 0 }, true)
                .subscribeAndPrint()
        // onNext -> [1, 2]
        // onNext -> [3, 4, 5]
        // onNext -> [6, 7, 8]
        // onNext -> [9, 10]
        // onComplete
    }

    @Test
    fun `groupBy - 스트림을 특정 조건을 만족할 때 스트림으로 분할하여 내보낸다`() {
        Flux
                .range(1, 10)
                .groupBy { it % 3 } // Flux<GroupedFlex<K, value>>
                .subscribe {
                    it
                            .map { value -> "key: ${it.key()}, value: $value" }
                            .subscribeAndPrint()
                }
        // onNext -> key: 1, value: 1
        // onNext -> key: 2, value: 2
        // onNext -> key: 0, value: 3
        // onNext -> key: 1, value: 4
        // onNext -> key: 2, value: 5
        // onNext -> key: 0, value: 6
        // onNext -> key: 1, value: 7
        // onNext -> key: 2, value: 8
        // onNext -> key: 0, value: 9
        // onNext -> key: 1, value: 10
        // onComplete
        // onComplete
        // onComplete
    }
}