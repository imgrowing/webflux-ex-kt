package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux

class PeekingEx {

    @Test
    fun `doOnNext - 각 원소에 대해 어떤 액션(Consumer)을 수행할 수 있다`() {
        Flux
                .just(1, 2, 3)
                .doOnNext { println("doOnNext() -> $it") }
                .subscribe()
        // doOnNext() -> 1
        // doOnNext() -> 2
        // doOnNext() -> 3
    }

    @Test
    fun `doOnComplete - complete 이벤트시 호출된다`() {
        Flux
                .just(1, 2, 3)
                .doOnComplete { println("doOnComplete()") }
                .subscribe()
        // doOnComplete()
    }

    @Test
    fun `doOnError - error 이벤트시 호출된다`() {
        Flux
                .just(1, 2, 3)
                .map { if (it < 3) it else (it / 0) }
                .doOnNext { println("doOnNext() : $it") }
                .doOnComplete { println("doOnComplete()") }
                .doOnError { println("doOnError() : $it") }
                .subscribe()
        // doOnNext() : 1
        // doOnNext() : 2
        // doOnError() : java.lang.ArithmeticException: / by zero
    }

    @Test
    fun `doOnTerminate - complete or error 이벤트시 호출된다`() {
        Flux
                .just(1, 2, 3)
                .doOnNext { println("doOnNext() : $it") }
                .doOnComplete { println("doOnComplete()") }
                .doOnError { println("doOnError() : $it") }
                .doOnTerminate { println("doOnTerminate()") }
                .subscribe()
        // doOnNext() : 1
        // doOnNext() : 2
        // doOnNext() : 3
        // doOnComplete()
        // doOnTerminate()

        Flux
                .just(1, 2, 3)
                .map { if (it < 3) it else (it / 0) }
                .doOnNext { println("doOnNext() : $it") }
                .doOnComplete { println("doOnComplete()") }
                .doOnError { println("doOnError() : $it") }
                .doOnTerminate { println("doOnTerminate()") }
                .subscribe()
        // doOnNext() : 1
        // doOnNext() : 2
        // doOnError() : java.lang.ArithmeticException: / by zero
        // doOnTerminate()
    }

    @Test
    fun `doOnEach - 모든 이벤트시 호출된다`() {
        Flux
                .just(1, 2, 3)
                .doOnEach { println("doOnEach() -> $it") }
                .subscribe()
        // doOnEach() -> doOnEach_onNext(1)
        // doOnEach() -> doOnEach_onNext(2)
        // doOnEach() -> doOnEach_onNext(3)
        // doOnEach() -> onComplete()

        Flux
                .just(1, 2, 3)
                .map { if (it < 3) it else (it / 0) }
                .doOnEach { println("doOnEach() -> $it") }
                .subscribe()
        // doOnEach() -> doOnEach_onNext(1)
        // doOnEach() -> doOnEach_onNext(2)
        // doOnEach() -> onError(java.lang.ArithmeticException: / by zero)
    }

    @Test
    fun `log - 모든 시그널을 로깅한다`() {
        Flux
                .just(1, 2, 3)
                .log()
                .subscribe()
        // 18:08:03.803 [main] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
        // 18:08:03.806 [main] INFO reactor.Flux.Array.1 - | request(unbounded)
        // 18:08:03.806 [main] INFO reactor.Flux.Array.1 - | onNext(1)
        // 18:08:03.806 [main] INFO reactor.Flux.Array.1 - | onNext(2)
        // 18:08:03.806 [main] INFO reactor.Flux.Array.1 - | onNext(3)
        // 18:08:03.806 [main] INFO reactor.Flux.Array.1 - | onComplete()

        Flux
                .just(1, 2, 3)
                .map { if (it < 3) it else (it / 0) }
                .log()
                .subscribe()
        // 18:08:03.809 [main] INFO reactor.Flux.MapFuseable.2 - | onSubscribe([Fuseable] FluxMapFuseable.MapFuseableSubscriber)
        // 18:08:03.810 [main] INFO reactor.Flux.MapFuseable.2 - | request(unbounded)
        // 18:08:03.811 [main] INFO reactor.Flux.MapFuseable.2 - | onNext(1)
        // 18:08:03.811 [main] INFO reactor.Flux.MapFuseable.2 - | onNext(2)
        // 18:08:03.815 [main] ERROR reactor.Flux.MapFuseable.2 - | onError(java.lang.ArithmeticException: / by zero)
        // 18:08:03.818 [main] ERROR reactor.Flux.MapFuseable.2 -
        // java.lang.ArithmeticException: / by zero
        // at ...
        // at ...
    }
}