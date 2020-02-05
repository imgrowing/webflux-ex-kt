package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import study.wf.subscribeAndPrint
import java.util.*

class TransformEx {

    @Test
    fun `transform - 동일한 순서의 연산자 적용 로직을 별도의 객체로 추출해서 재사용한다`() {
        // transformer: Function<Flux<T>, Publisher<V>>
        // 사용자의 순번과 아이디를 출력하고, 원본 원소 그대로를 반환하는 변환함수
        val logUserInfo: (Flux<String>) -> Flux<String> = { stream: Flux<String> ->
            stream
                    .index()
                    .doOnNext { println("[${it.t1}] User: ${it.t2}") }
                    .map { it.t2 }
        }

        Flux
                .range(1001, 3)
                .map { "user-$it" }
                .transform(logUserInfo)
                .subscribeAndPrint()

        // [0] User: user-1001
        // onNext -> user-1001
        // [1] User: user-1002
        // onNext -> user-1002
        // [2] User: user-1003
        // onNext -> user-1003
        // onComplete
    }

    @Test
    fun `transformDeferred1 == composer - 지연 실행, 동일한 순서의 연산자 적용 로직을 별도의 객체로 추출해서 재사용한다`() {
        // transformer: Function<Flux<T>, Publisher<V>>
        // 사용자의 순번과 아이디를 출력하고, 원본 원소 그대로를 반환하는 변환함수
        val logUserInfo: (Flux<String>) -> Flux<String> = { stream: Flux<String> ->
            stream
                    .index()
                    .doOnNext { println("[${it.t1}] User: ${it.t2}") }
                    .map { it.t2 }
        }

        Flux
                .range(1001, 3)
                .map { "user-$it" }
                .transformDeferred(logUserInfo)
                .subscribeAndPrint()

        // [0] User: user-1001
        // onNext -> user-1001
        // [1] User: user-1002
        // onNext -> user-1002
        // [2] User: user-1003
        // onNext -> user-1003
        // onComplete
    }

    @Test
    fun `transformDeferred2 == composer - 지연 실행, 동일한 순서의 연산자 적용 로직을 별도의 객체로 추출해서 재사용한다`() {
        // transformer: Function<Flux<T>, Publisher<V>>
        // 사용자의 순번과 아이디를 출력하고, 원본 원소 그대로를 반환하는 변환함수
        val random = Random()

        val logUserInfo: (Flux<Int>) -> Flux<Int> = { stream: Flux<Int> ->
            if (random.nextBoolean()) {
                stream.doOnNext { println("[path A] User: $it") }
            } else {
                stream.doOnNext { println("[path B] User: $it") }
            }
        }

        val publisher = Flux
                .range(1001, 3)
                .transformDeferred(logUserInfo)

        publisher.subscribe()
        publisher.subscribe()

        // subscribe 시점까지 transformer의 결정이 지연되기 때문에, subscribe 할 때 마다 transformer 내용이 달라질 수 있다.
        // [path B] User: 1001
        // [path B] User: 1002
        // [path B] User: 1003
        // [path A] User: 1001
        // [path A] User: 1002
        // [path A] User: 1003

        // [path A] User: 1001
        // [path A] User: 1002
        // [path A] User: 1003
        // [path B] User: 1001
        // [path B] User: 1002
        // [path B] User: 1003
    }

    @Test
    fun `transform2 - 동일한 순서의 연산자 적용 로직을 별도의 객체로 추출해서 재사용한다`() {
        // transformer: Function<Flux<T>, Publisher<V>>
        // 사용자의 순번과 아이디를 출력하고, 원본 원소 그대로를 반환하는 변환함수
        val random = Random()

        val logUserInfo: (Flux<Int>) -> Flux<Int> = { stream: Flux<Int> ->
            if (random.nextBoolean()) {
                stream.doOnNext { println("[path A] User: $it") }
            } else {
                stream.doOnNext { println("[path B] User: $it") }
            }
        }

        val publisher = Flux
                .range(1001, 3)
                .transform(logUserInfo)

        publisher.subscribe()
        publisher.subscribe()

        // transformer를 참조하는 시점에 transformer이 결정되기 때문에, 여러 번 subscribe 를 해도 항상 동일한 transformer 내용이 적용된다
        // [path A] User: 1001
        // [path A] User: 1002
        // [path A] User: 1003
        // [path A] User: 1001
        // [path A] User: 1002
        // [path A] User: 1003

        // [path B] User: 1001
        // [path B] User: 1002
        // [path B] User: 1003
        // [path B] User: 1001
        // [path B] User: 1002
        // [path B] User: 1003
    }
}