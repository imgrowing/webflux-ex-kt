package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import java.time.Duration

class FilteringEx {
    @Test
    fun `filter - simple`() {
        Flux
                .range(1, 5)
                .filter { i -> i % 2 == 0 }
                .subscribe(::println)
    }

    @Test
    fun `ignoreElements - 모든 원소를 무시한다, onComplete만 발생함`() {
        Flux
                .range(1, 5)
                .ignoreElements()
                .doOnTerminate { println("onComplete") }
                .subscribe { println("onNext") } // 호출되지 않음
    }

    @Test
    fun `takeLast - 마지막 원소 n개를 반환한다`() {
        Flux
                .range(1, 5)
                .takeLast(2)
                .subscribe { println("onNext -> $it") }
    }

    @Test
    fun `takeUntil - 어떤 조건의 만족이 발생할 때 까지 원소를 반환하다가 그 이후에 onComplete 한다`() {
        Flux
                .range(1, 5)
                .takeUntil { it >= 3 }
                .subscribe { println("onNext -> $it") }
        // 1, 2, 3, onComplete
    }

    @Test
    fun `elementAt - n번째 원소만 반환한다, zero-based index`() {
        Flux
                .range(0, 5)
                .elementAt(3)
                .subscribe { println("onNext -> $it") }
        // 3, onComplete
    }

    @Test
    fun `single - 단일 원소를 내보낸다`() {
        Flux
                .just(4)
                .single()
                .subscribe { println("onNext -> $it") }
        // 4, onComplete

        Flux
                .range(0, 1)
                .single()
                .subscribe { println("onNext -> $it") }
        // 0, onComplete

        Flux
                .range(0, 5)
                .single()
                .subscribe { println("onNext -> $it") }
        // IndexOutOfBoundsException: Source emitted more than one item
        // -> 여러 개의 원소가 있는 경우에는 에러 발생함
    }

    @Test
    fun `skip - 특정 기간 동안 skip한다, skip이 끝나면 원소를 방출한다`() {
        Flux
                .range(1, 5)
                .delayElements(Duration.ofMillis(100))
                .skip(Duration.ofMillis(150))
                .subscribe { println("onNext -> $it") }

        Thread.sleep(600)
        // 2, 3, 4, 5
    }

    @Test
    fun `take - 특정 기간 동안만 원소를 방출한다, 그 이후에는 종료한다`() {
        Flux
                .range(1, 5)
                .delayElements(Duration.ofMillis(100))
                .take(Duration.ofMillis(250))
                .subscribe { println("onNext -> $it") }

        Thread.sleep(600)
        // 1, 2
    }
}