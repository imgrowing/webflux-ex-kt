package study.wf

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger

const val DEFAULT_VALUE = "DEFAULT_VALUE"
const val TIME_OUT_MESSAGE = "TIME_OUT"
const val TIME_OUT_DEFAULT_VALUE = "TIME_OUT_DEFAULT_VALUE"

/*
아래 부분의 예제를 재구성한 것임
https://projectreactor.io/docs/core/release/reference/index.html#error.handling
 */
class HandlingErrorsTest {

    fun callExternalService(key: String): Mono<String> {
        return when {
            key.startsWith("timeout") -> Mono.error(TimeoutException(TIME_OUT_MESSAGE))
            key.startsWith("unknown") -> Mono.error(IllegalArgumentException())
            else -> Mono.just("value" + key.removePrefix("key"))
        }
    }

    fun getFromCache(key: String): Mono<String> {
        return Mono.just("cached value")
    }

    @Test
    fun `정상 완료 케이스`() {
        val flux = Flux.just("key1", "key2")
                .flatMap { key -> callExternalService(key) }

        StepVerifier.create(flux.log())
                .expectNext("value1", "value2")
                .verifyComplete()
    }

    @Test
    fun `fallback onErrorReturn() - 대체값 반환`() {
        val flux = Flux.just("key1", "key2", "timeout", "key4")
                .flatMap { key ->
                    callExternalService(key)
                            .onErrorReturn(DEFAULT_VALUE)
                }

        StepVerifier.create(flux.log())
                .expectNext("value1", "value2", DEFAULT_VALUE, "value4")
                .verifyComplete()
    }

    @Test
    fun `fallback onErrorReturn() - 특정 exception 이면 대체값 반환`() {
        val flux = Flux.just("key1", "key2", "timeout", "key4")
                .flatMap {
                    key -> callExternalService(key)
                        .onErrorReturn(TimeoutException::class.java,  TIME_OUT_DEFAULT_VALUE)
                }

        StepVerifier.create(flux.log())
                .expectNext("value1", "value2", TIME_OUT_DEFAULT_VALUE, "value4")
                .verifyComplete()
    }

    @Test
    fun `fallback onErrorReturn() - 특정 exception message 이면 대체값 반환`() {
        val flux = Flux.just("key1", "key2", "timeout", "key4")
                .flatMap {
                    key -> callExternalService(key)
                        .onErrorReturn({it.message == TIME_OUT_MESSAGE},  TIME_OUT_DEFAULT_VALUE)
                }

        StepVerifier.create(flux.log())
                .expectNext("value1", "value2", TIME_OUT_DEFAULT_VALUE, "value4")
                .verifyComplete()
    }

    @Test
    fun `fallback onErrorResume() - 고정된 function 호출을 대체값으로 반환`() {
        val flux = Flux.just("key1", "key2", "timeout", "key4")
                .flatMap { key -> callExternalService(key)
                            .onErrorResume { getFromCache(key) }
                }

        StepVerifier.create(flux.log())
                .expectNext("value1", "value2", "cached value", "value4")
                .verifyComplete()
    }

    @Test
    fun `fallback onErrorResume() - 조건에 따라 대체값 반환`() {
        val flux = Flux.just("key1", "key2", "timeout", "key4", "unknown")
                .flatMap {
                    key -> callExternalService(key)
                        .onErrorResume { e ->
                            when (e) {
                                is TimeoutException -> getFromCache(key)
                                is java.lang.IllegalArgumentException -> Mono.just(DEFAULT_VALUE)
                                else -> Mono.error(RuntimeException())
                            }
                        }
                }

        StepVerifier.create(flux.log())
                .expectNext("value1", "value2", "cached value", "value4", DEFAULT_VALUE)
                .verifyComplete()
    }

    fun fromError(error: Throwable): Mono<String> {
        return when (error) {
            is TimeoutException -> getFromCache("")
            is java.lang.IllegalArgumentException -> Mono.just(DEFAULT_VALUE)
            else -> Mono.error(RuntimeException())
        }
    }

    @Test
    fun `fallback onErrorResume() - error를 분석하여 대체값 반환`() {
        val flux = Flux.just("key1", "key2", "timeout", "key4", "unknown")
                .flatMap {
                    key -> callExternalService(key)
                        .onErrorResume { fromError(it) }
                }

        StepVerifier.create(flux.log())
                .expectNext("value1", "value2", "cached value", "value4", DEFAULT_VALUE)
                .verifyComplete()
    }

    @Test
    fun `doOnError() - 에러가 발생했을 때, 흐름은 그대로 가게 하고, 어떤 일(로깅, 카운팅 등)을 하고 싶을 때`() {
        val failureCount = AtomicInteger()

        val flux = Flux.just("key1", "key2", "timeout", "key4")
                .flatMap { key -> callExternalService(key)
                        .doOnError {
                            failureCount.incrementAndGet()
                            println(">>>>>>>>>>>>>>>>>>>> 에러가 발생함: ${it.javaClass} - ${it.message}")
                        }
                }

        StepVerifier.create(flux.log())
                .expectNext("value1", "value2")
                .expectError(TimeoutException::class.java)
                .verify()

        assert(failureCount.get() == 1)
    }

    @Test
    fun `doFinally() - 취소되었는지 확인한다`() {
        val flux = Flux.just("one", "two")
                .doFinally { signalType ->
                    println(">>>>>>>>>>>> doFinally() 진입함")
                    println(">>>>>>>>>>>> signalType: $signalType") // "cancel" - 원본 Flux는 cancel됨
                }
                .take(1) // 1개의 item이 방출되면 source flux에게 cancel을 호출함

        StepVerifier.create(flux.log())
                .expectNext("one")
                .verifyComplete()
    }

    @Test
    fun `retry() - 시퀀스를 다시 구독한다`() {
        val flux = Flux.interval(Duration.ofMillis(250))
                .map { num ->
                    if (num < 3) "tick $num" else throw RuntimeException("boom")
                }
                .retry(1) // 1번 더 re-subscribe 한다.

        StepVerifier.create(flux.log())
                .expectNext("tick 0", "tick 1", "tick 2", "tick 0", "tick 1", "tick 2")
                .verifyErrorMatches { e -> e is RuntimeException && e.message == "boom" }

        Thread.sleep(2100)  // Flux.interval()은 다른 스레드에서 비동기로 실행됨. 따라서 main thread에서 기다려야 함
    }

}