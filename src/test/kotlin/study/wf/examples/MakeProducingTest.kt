package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.Math.abs
import java.time.Duration
import java.util.*
import java.util.stream.Stream

class MakeProducingTest {
    @Test
    fun `Flux just - single element`() {
        Flux
                .just(1)
                .subscribe { println(it) }
    }

    @Test
    fun `Flux just - elements`() {
        Flux
                .just(1, 2, 3)
                .subscribe { println(it) }
    }

    @Test
    fun `Flux range`() {
        Flux
                .range(101, 5)
                .subscribe { println(it) }
    }

    @Test
    fun `Flux empty`() {
        Flux
                .empty<String>()
                .subscribe { println(it) }
    }

    @Test
    fun `Flux error`() {
        Flux
                .error<RuntimeException>(RuntimeException("error"))
                .doOnError { println("doOnError: $it") }
                .subscribe { println("onNext") }
    }

    @Test
    fun `Flux fromArray`() {
        Flux
                .fromArray(arrayOf(1, 2, 3))
                .subscribe { println("onNext -> $it") }
    }

    @Test
    fun `Flux fromIterable`() {
        Flux
                .fromIterable(listOf(1, 2, 3))
                .subscribe { println("onNext -> $it") }
    }

    @Test
    fun `Flux fromStream`() {
        Flux
                .fromStream(Stream.of(1, 2, 3))
                .subscribe { println("onNext -> $it") }
    }

    @Test
    fun `Flux from publisher`() {
        Flux
                .from(Flux.just(1, 2, 3))
                .subscribe { println("onNext -> $it") }

        Flux
                .from(Mono.just(11))
                .subscribe { println("onNext -> $it") }
    }

    @Test
    fun `defer - Mono element의 생성을 실행 시점까지 지연시킨다, 실행 시점에 defer {  } 가 계산된다`() {
        val deferMono = Mono
                .defer {
                    randomDelay()
                    Mono.just(Date().time % 10)
                }

        deferMono.subscribe { println("1st defer -> $it") }
        deferMono.subscribe { println("2nd defer -> $it") }
        deferMono.subscribe { println("3rd defer -> $it") }

        randomDelay()

        val normalMono = Mono.just(Date().time % 10)

        normalMono.subscribe { println("1st mono -> $it") }
        normalMono.subscribe { println("2nd mono -> $it") }
        normalMono.subscribe { println("3rd mono -> $it") }
    }

    private fun randomDelay() {
        Thread.sleep(abs(Random().nextLong()) % 10)
    }

    @Test
    fun `inteval - 별도의 스케줄러에서 실행되므로, sleep을 주어야 실행 결과를 확인할 수 있다`() {
        var counter = 0
        Flux
                .interval(Duration.ofMillis(100))
                .map {
                    "interval: " + ++counter
                }
                .subscribe { println(it) }

        Thread.sleep(510)
    }

    @Test
    fun `repeat - 반복해서 재구독한다`() {
        Flux
                .just(1)
                .repeat()
                .take(5)
                .subscribe { println("onNext -> $it") }

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
                .subscribe { println("onNext -> $it") }

        // 총 2회의 구독이 발생함
        // onNext -> 1
        // onNext -> 1
    }

    @Test
    fun `defaultIfEmpty - 스트림이 비어 있으면 기본값을 반환한다`() {
        Flux
                .empty<Int>()
                .defaultIfEmpty(-1)
                .subscribe { println("onNext -> $it") }

        // onNext -> -1
    }
}