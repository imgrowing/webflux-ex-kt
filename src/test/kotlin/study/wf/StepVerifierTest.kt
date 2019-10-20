package study.wf

import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.expectError
import reactor.test.publisher.PublisherProbe
import java.time.Duration

class StepVerifierTest {

    @Test
    fun `(foo, bar) 를 검증한다 - expectComplete와 verify를 사용한다`() {
        StepVerifier.create(Flux.just("foo", "bar").log())
                .expectNext("foo")
                .expectNext("bar")
                .expectComplete()
                .verify()
    }

    @Test
    fun `(foo, bar) 를 검증한다 - verifyComplete를 사용한다`() {
        StepVerifier.create(Flux.just("foo", "bar"))
                .expectNext("foo")
                .expectNext("bar")
                .verifyComplete()
    }

    @Test
    fun `(foo, bar) 를 검증한다 - expectNext(multi value)를 사용한다`() {
        StepVerifier.create(Flux.just("foo", "bar", "completed").log())
                .expectNext("foo", "bar", "completed") // 다음에 방출되는 시그널은 t1, t2, t3 이다.
                .verifyComplete()
    }

    @Test
    fun `조회 결과가 empty 이면 특정 처리를 수행한다`() {
        val emptyStr: String? = null
        val emptyMono = Mono.justOrEmpty(emptyStr)
                .hasElement()
                .map { hasNoElements ->
                    if (hasNoElements) "not empty"
                    else "empty"
                }

        StepVerifier.create(emptyMono.log())
                .expectNext("empty")
                .verifyComplete()
    }

    @Test
    fun `조회 결과가 not empty 이면 특정 처리를 수행한다`() {
        val notEmptyStr: String? = "dummy"
        val notEmptyMono = Mono.justOrEmpty(notEmptyStr)
                .hasElement()
                .map { monoHasElements ->
                    if (monoHasElements) "not empty"
                    else "empty"
                }

        StepVerifier.create(notEmptyMono.log())
                .expectNext("not empty")
                .verifyComplete()
    }

    @Test
    fun `조회 결과가 empty 이면 exception, 그렇지 않으면 특정 처리를 한다`() {
        val nullableStr: String? = null
        val emptyOrNotMono = Mono.justOrEmpty(nullableStr)
                .hasElement()
                .map { hasNoElements ->
                    if (!hasNoElements) throw RuntimeException("empty")
                    else "do something if not empty"
                }

        StepVerifier.create(emptyOrNotMono.log())
                .expectError(RuntimeException::class)
                .verify()
    }

    @Test
    fun `조회 결과가 empty 이면 특정 처리를 한다, 그렇지 않으면 exception`() {
        val nullableStr: String? = "not null"
        val emptyOrNotMono = Mono.justOrEmpty(nullableStr)
                .hasElement()
                .map { hasNoElements ->
                    if (!hasNoElements) throw RuntimeException("empty")
                    else "do something if not empty"
                }

        StepVerifier.create(emptyOrNotMono.log())
                .expectNext("do something if not empty")
                .verifyComplete()
    }

    @Test
    fun `Mono가 empty인 경우 map에 진입하는지 테스트`() {
        val emptyMono = Mono.empty<String>()
                .map { "invoked" }
                .defaultIfEmpty("not invoked")

        StepVerifier.create(emptyMono.log())
                .expectNext("not invoked")
                .verifyComplete()
    }

    @Test
    fun `Mono가 empty인 경우 flatMap에 진입하는지 테스트`() {
        val emptyMono = Mono.empty<String>()
                .flatMap { Mono.just("invoked") }
                .defaultIfEmpty("not invoked")

        StepVerifier.create(emptyMono.log())
                .expectNext("not invoked")
                .verifyComplete()
    }

    @Test
    fun testAppendBoomError() {
        val source = Flux.just("thing1", "thing2")

        StepVerifier.create(appendBoomError(source).log()) // Publisher 를 전달하여 StepVerifier 를 생성한다.
                .expectNext("thing1") // next()에 전달되는 값은
                .expectNext("thing2") // next()에 전달되는 값은
                .expectErrorMessage("boom") // 에러가 발생하며, 메시지는
                .verify() // 최종(complete/error/cancellation) 이벤트에 대한 검증을 지정한 후 verify()를 호출해야 test 가 시작된다.
    }

    fun <T> appendBoomError(source: Flux<T>): Flux<T> {
        return source.concatWith(Mono.error(IllegalArgumentException("boom")))
    }

    @Test
    fun `withVirtualTime()으로 시간을 조작하여 테스트한다`() {
        StepVerifier.withVirtualTime { Mono.delay(Duration.ofDays(1)).log() }
                .expectSubscription()  // subscription(onSubscribe) 도 event 로 인식된다.
                .expectNoEvent(Duration.ofDays(1)) // 1일 동안 이벤트가 없다.
                .expectNext(0)  // 0이 방출된다.
                .verifyComplete()  // complete 된다.
    }

    fun processOrFallback(source: Mono<String>, fallback: Publisher<String>): Flux<String> {
        return source
                .flatMapMany { Flux.fromIterable(it.split("\\s+".toRegex())) }
                .switchIfEmpty(fallback)  // Mono.switchIfEmpty(Mono<T> alternate) - 이 Mono가 빈 채로 complete 되면 다른 Mono로 대체한다.
    }

    @Test
    fun `기본 흐름이 사용되었다`() {
        StepVerifier.create(processOrFallback(
                Mono.just("just a phrase with    tabs!"),
                Mono.just("EMPTY_PHRASE")
        ))
                .expectNext("just", "a", "phrase", "with", "tabs!")
                .verifyComplete()
    }

    @Test
    fun `empty 흐름이 사용되었다`() {
        StepVerifier.create(
                processOrFallback(
                        Mono.empty(),
                        Mono.just("EMPTY_PHRASE")
                )
        )
                .expectNext("EMPTY_PHRASE")
                .verifyComplete()
    }

    fun executeCommand(command: String): Mono<String> {
        return Mono.just("$command DONE")
    }

    fun processOrFallback2(commandSource: Mono<String>, doWhenEmpty: Mono<Void>): Mono<Void> {
        return commandSource
                .flatMap { // Mono.flatMap(Function<T, R> transformer): <T>를 Mono<R>로 변환하여 방출한다. 비동기로 동작한다.
                    command -> executeCommand(command).then() // then()은 시그널에는 관심이 없고, 종료(complete/error) 시그널만 다시 방출한다.
                }
                .switchIfEmpty(doWhenEmpty)
    }

    @Test
    fun `empty path가 사용되었다`() {
        // PublisherProbe.empty() - 단순히 complete 시그널만 방출(Flux/Mono)하는 PublisherProbe를 생성한다.
        // 생성된 probe는 subscription, cancellation, request 이벤트를 캡쳐한다.
        // 실행 경로를 탐지도 해야 하고, 데이터도 방출해야 한다면, PublisherProbe.of(Publisher<T>)를 사용해서 publisher를 감싸서 사용하면 된다.
        val publisherProbe: PublisherProbe<Void> = PublisherProbe.empty()

        StepVerifier
                .create(processOrFallback2(Mono.empty(), publisherProbe.mono())) // PublisherProbe.mono()/flux() - probe의 Mono/Flux 를 반환한다.
                .verifyComplete()

        // 시퀀스가 종료된 후에 assert 할 수 있다.
        publisherProbe.assertWasSubscribed()    // probe가 subscribe 하였다.
        publisherProbe.assertWasRequested()     // probe가 request 하였다.
        publisherProbe.assertWasNotCancelled()  // probe가 취소되지 않았다.
    }
}