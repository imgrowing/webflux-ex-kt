package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import study.wf.subscribeAndPrint

class ReducingEx {

    @Test
    fun `count - 원소 개수 세기`() {
        Flux
                .just(1, 2, 3)
                .count()
                .subscribeAndPrint()
        /*
        onNext -> 3
        onComplete
         */
    }

    @Test
    fun `all predicate - boolean, 모든 원소가 조건을 만족하는지 여부 - true`() {
        Flux
                .just(1, 3, 5)
                .all { it % 2 == 1 }
                .subscribeAndPrint()
        /*
        onNext -> true
        onComplete
         */
    }

    @Test
    fun `all predicate - boolean, 모든 원소가 조건을 만족하는지 여부 - false`() {
        Flux
                .just(1, 3, 4, 5)
                .all { it % 2 == 1 }
                .subscribeAndPrint()
        /*
        onNext -> false
        onComplete
         */
    }

    @Test
    fun `any predicate - boolean, 하나의 원소라도 조건을 만족하는지 여부 - true`() {
        Flux
                .just(1, 2, 3, 4)
                .any { it % 2 == 1 }
                .subscribeAndPrint()
        /*
        onNext -> true
        onComplete
         */
    }

    @Test
    fun `any predicate - boolean, 하나의 원소라도 조건을 만족하는지 여부 - false`() {
        Flux
                .just(2, 4, 6, 8)
                .any { it % 2 == 1 }
                .subscribeAndPrint()
        /*
        onNext -> false
        onComplete
         */
    }

    @Test
    fun `hasElement value - boolean, 특정 원소가 존재하는지 여부`() {
        Flux
                .just(1, 2, 3)
                .hasElement(3)
                .subscribeAndPrint()
        /*
        onNext -> true
        onComplete
         */
    }

    @Test
    fun `hasElements - boolean, 스트림에 원소가 존재하는지 여부`() {
        Flux
                .just(1, 2, 3)
                .hasElements()
                .subscribeAndPrint()
        /*
        onNext -> true
        onComplete
         */

        Flux
                .empty<Int>()
                .hasElements()
                .subscribeAndPrint()
        /*
        onNext -> false
        onComplete
         */
    }

    @Test
    fun `sort - 모든 원소를 정렬하여 내보낸다`() {
        Flux
                .just(3, 5, 2, 1, 4)
                .sort()
                .subscribeAndPrint()

        /*
        onNext -> 1
        onNext -> 2
        onNext -> 3
        onNext -> 4
        onNext -> 5
         */
    }

    @Test
    fun `reduce - 값을 누적하여 하나의 값만 내보낸다`() {
        Flux
                .range(1, 10)
                .reduce(0) { prevAcc: Int, nextValue: Int -> prevAcc + nextValue }
                .subscribeAndPrint()
        /*
        onNext -> 55
        onComplete
         */
    }


    @Test
    fun `scan - 값을 누적하면서 누적된 값을 계속 내보낸다`() {
        Flux
                .range(1, 10)
                .scan(0) { prevAcc: Int, nextValue: Int -> prevAcc + nextValue }
                .subscribeAndPrint()
        /*
        onNext -> 0
        onNext -> 1
        onNext -> 3
        onNext -> 6
        onNext -> 10
        onNext -> 15
        onNext -> 21
        onNext -> 28
        onNext -> 36
        onNext -> 45
        onNext -> 55
        onComplete
         */
    }

    @Test
    fun `then - 상위 스트림이 완료될 때 하나의 Mono를 내보낸다`() {
        Flux
                .just(1, 2, 3)
                .then(Mono.just(9))
                .subscribeAndPrint()
        /*
        onNext -> 9
        onComplete
         */
    }

    @Test
    fun `thenMany - 상위 스트림이 완료될 때 하나의 publisher를 내보낸다`() {
        Flux
                .just(1, 2, 3)
                .thenMany(Flux.range(11, 3))
                .subscribeAndPrint()
        /*
        onNext -> 11
        onNext -> 12
        onNext -> 13
        onComplete
         */
    }

    @Test
    fun `thenEmpty - 상위 스트림이 완료될 때 아무런 원소 없이 완료신호를 내보낸다`() {
        Flux
                .just(1, 2, 3)
                .thenEmpty(Mono.empty())
                .subscribeAndPrint()
        /*
        onComplete
         */
    }
}