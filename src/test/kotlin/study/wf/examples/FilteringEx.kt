package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import study.wf.subscribeAndPrint
import java.time.Duration

class FilteringEx {
    @Test
    fun `filter - simple`() {
        Flux
                .range(1, 5)
                .filter { i -> i % 2 == 0 }
                .subscribeAndPrint()
        // onNext -> 2
        // onNext -> 4
        // onComplete
    }

    @Test
    fun `ignoreElements - 모든 원소를 무시한다, onComplete만 발생함`() {
        Flux
                .range(1, 5)
                .ignoreElements()
                .subscribeAndPrint()
        // onComplete
    }

    @Test
    fun `takeLast - 마지막 원소 n개를 반환한다`() {
        Flux
                .range(1, 5)
                .takeLast(2)
                .subscribeAndPrint()
        // onNext -> 4
        // onNext -> 5
        // onComplete
    }

    @Test
    fun `takeUntil - 어떤 조건의 만족이 발생할 때 까지 원소를 반환하다가 그 이후에 onComplete 한다`() {
        Flux
                .range(1, 5)
                .takeUntil { it >= 3 }
                .subscribeAndPrint()
        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onComplete
    }

    @Test
    fun `elementAt - n번째 원소만 반환한다, zero-based index`() {
        Flux
                .range(0, 5)
                .elementAt(3)
                .subscribeAndPrint()
        // onNext -> 3
        // onComplete
    }

    @Test
    fun `single - 단일 원소를 내보낸다`() {
        Flux
                .just(4)
                .single()
                .subscribeAndPrint()
        // onNext -> 4
        // onComplete

        Flux
                .range(0, 1)
                .single()
                .subscribeAndPrint()
        // onNext -> 0
        // onComplete

        Flux
                .range(0, 5)
                .single()
                .subscribeAndPrint()
        // onError -> java.lang.IndexOutOfBoundsException: Source emitted more than one item
        // -> 여러 개의 원소가 있는 경우에는 에러 발생함
    }

    @Test
    fun `skip - 특정 기간 동안 skip한다, skip이 끝나면 원소를 방출한다`() {
        Flux
                .range(1, 5)
                .delayElements(Duration.ofMillis(100))
                .skip(Duration.ofMillis(150))
                .subscribeAndPrint()

        Thread.sleep(600)
        // onNext -> 2
        // onNext -> 3
        // onNext -> 4
        // onNext -> 5
        // onComplete
    }

    @Test
    fun `skip - 지정된 갯수 만큼 skip한다, skip이 끝나면 원소를 방출한다`() {
        Flux
                .range(1, 5)
                .delayElements(Duration.ofMillis(100))
                .skip(2)
                .subscribeAndPrint()

        Thread.sleep(600)
        // onNext -> 3
        // onNext -> 4
        // onNext -> 5
        // onComplete
    }

    @Test
    fun `take - 특정 기간 동안만 원소를 방출한다, 그 이후에는 종료한다`() {
        Flux
                .range(1, 5)
                .delayElements(Duration.ofMillis(100))
                .take(Duration.ofMillis(250))
                .subscribeAndPrint()

        Thread.sleep(600)
        // onNext -> 1
        // onNext -> 2
        // onComplete
    }

    @Test
    fun `sample - 전체 원소 중 일부만 샘플링한다, 시간 구간 마다 마지막 원소를 방출한다`() {
        Flux
                .range(1, 100)
                .delayElements(Duration.ofMillis(10))
                .sample(Duration.ofMillis(100)) // 최근 100ms 구간 중 맨 마지막 원소만 발생시킨다
                .subscribeAndPrint()
        Thread.sleep(1100)
        // onNext -> 5
        // onNext -> 13
        // onNext -> 22
        // onNext -> 32
        // onNext -> 40
        // onNext -> 49
        // onNext -> 59
        // onNext -> 67
        // onNext -> 76
        // onNext -> 84
        // onNext -> 93
    }

}