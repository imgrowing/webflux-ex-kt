package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import study.wf.subscribeAndPrint
import java.lang.Math.abs
import java.time.Duration
import java.util.*
import java.util.stream.Stream

class MakeProducingTest {
    @Test
    fun `Flux just - single element`() {
        Flux
                .just(1)
                .subscribeAndPrint()
        // onNext -> 1
        // onComplete
    }

    @Test
    fun `Flux just - elements`() {
        Flux
                .just(1, 2, 3)
                .subscribeAndPrint()
        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onComplete
    }

    @Test
    fun `Flux range`() {
        Flux
                .range(101, 5)
                .subscribeAndPrint()
        // onNext -> 101
        // onNext -> 102
        // onNext -> 103
        // onNext -> 104
        // onNext -> 105
        // onComplete
    }

    @Test
    fun `Flux empty`() {
        Flux
                .empty<String>()
                .subscribeAndPrint()
        // onComplete
    }

    @Test
    fun `Flux error`() {
        Flux
                .error<RuntimeException>(RuntimeException("error"))
                .subscribeAndPrint()
        // onError -> java.lang.RuntimeException: error
    }

    @Test
    fun `Flux fromArray`() {
        Flux
                .fromArray(arrayOf(1, 2, 3))
                .subscribeAndPrint()
        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onComplete
    }

    @Test
    fun `Flux fromIterable`() {
        Flux
                .fromIterable(listOf(1, 2, 3))
                .subscribeAndPrint()
        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onComplete
    }

    @Test
    fun `Flux fromStream`() {
        Flux
                .fromStream(Stream.of(1, 2, 3))
                .subscribeAndPrint()
        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onComplete
    }

    @Test
    fun `Flux from publisher`() {
        Flux
                .from(Flux.just(1, 2, 3))
                .subscribeAndPrint()
        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onComplete

        Flux
                .from(Mono.just(11))
                .subscribeAndPrint()
        // onNext -> 11
        // onComplete
    }

    @Test
    fun `defer - Mono element의 생성을 실행 시점까지 지연시킨다, 실행 시점에 defer {  } 가 계산된다`() {
        val deferMono = Mono
                .defer {
                    randomDelay()
                    Mono.just(Date().time % 10)
                }

        deferMono.subscribeAndPrint()
        deferMono.subscribeAndPrint()
        deferMono.subscribeAndPrint()
        // onNext -> 5
        // onComplete
        // onNext -> 8
        // onComplete
        // onNext -> 0
        // onComplete

        randomDelay()

        val normalMono = Mono.just(Date().time % 10)

        normalMono.subscribeAndPrint()
        normalMono.subscribeAndPrint()
        normalMono.subscribeAndPrint()
        // onNext -> 5
        // onComplete
        // onNext -> 5
        // onComplete
        // onNext -> 5
        // onComplete
    }

    private fun randomDelay() {
        Thread.sleep(abs(Random().nextLong()) % 10)
    }

    @Test
    fun `interval - 별도의 스케줄러에서 실행되므로, sleep을 주어야 실행 결과를 확인할 수 있다`() {
        var counter = 0
        Flux
                .interval(Duration.ofMillis(100))
                .map {
                    "interval: " + ++counter
                }
                .subscribeAndPrint()

        Thread.sleep(510)
        // onNext -> interval: 1
        // onNext -> interval: 2
        // onNext -> interval: 3
        // onNext -> interval: 4
        // onNext -> interval: 5
    }

    @Test
    fun `repeat - 반복해서 재구독한다`() {
        Flux
                .just(1)
                .repeat()
                .take(5)
                .subscribeAndPrint()

        // onNext -> 1
        // onNext -> 1
        // onNext -> 1
        // onNext -> 1
        // onNext -> 1
    }

    @Test
    fun `repeat n - n회 재구독한다, 실제로 emit된 후에 n회 재구독 하기 때문에, 최종 구독 횟수는 n+1 회가 된다`() {
        Flux
                .just(1)
                .repeat(1)
                .subscribeAndPrint()

        // 총 2회의 구독이 발생함
        // onNext -> 1
        // onNext -> 1
    }

    @Test
    fun `defaultIfEmpty - 스트림이 비어 있으면 기본값을 반환한다`() {
        Flux
                .empty<Int>()
                .defaultIfEmpty(-1)
                .subscribeAndPrint()

        // onNext -> -1
    }
}