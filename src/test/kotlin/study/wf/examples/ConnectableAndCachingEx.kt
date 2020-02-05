package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import reactor.util.Loggers
import java.time.Duration

class ConnectableAndCachingEx {

    private val log = Loggers.getLogger(this.javaClass)

    @Test
    fun `ConnectableFlux - 1회 구독으로 다수의 구독자에게 공유`() {
        val source: Flux<Int> = Flux
                .range(0, 3)
                .doOnSubscribe { log.info("cold publisher에 대해 새로운 subscription 발생") }

        val connectableFlux: ConnectableFlux<Int> = source.publish()

        connectableFlux.subscribe { log.info("[Subscriber 1] onNext: $it") }
        connectableFlux.subscribe { log.info("[Subscriber 2] onNext: $it") }

        log.info("all subscribers are ready, connecting")
        connectableFlux.connect()   // connect()를 호출해야 connectableFlux에 대한 subscribe()가 동작한다

        // all subscribers are ready, connecting
        // cold publisher에 대해 새로운 subscription 발생
        // [Subscriber 1] onNext: 0
        // [Subscriber 2] onNext: 0
        // [Subscriber 1] onNext: 1
        // [Subscriber 2] onNext: 1
        // [Subscriber 1] onNext: 2
        // [Subscriber 2] onNext: 2
    }

    @Test
    fun `cache - 스트림의 내용을 캐시한다, 내부적으로 connectableFlux를 사용한다`() {
        val source: Flux<Int> = Flux
                .range(0, 3)
                .doOnSubscribe { log.info("cold publisher에 대해 새로운 subscription 발생") }

        val cachedSource: Flux<Int> = source.cache(Duration.ofSeconds(1)) // 1초 캐시

        cachedSource.subscribe { log.info("[Subscriber 1] onNext: $it") } // 구독 발생, 캐시됨
        cachedSource.subscribe { log.info("[Subscriber 2] onNext: $it") } // 캐시에 있기 때문에 구독 발생하지 않고, 캐시에서 읽어옴

        Thread.sleep(1200) // 캐시의 타임아웃 발생

        cachedSource.subscribe { log.info("[Subscriber 3] onNext: $it") } // 구독 발생 (이미 캐시 타임아웃이 발생했음)

        // cold publisher에 대해 새로운 subscription 발생
        // [Subscriber 1] onNext: 0
        // [Subscriber 1] onNext: 1
        // [Subscriber 1] onNext: 2
        // [Subscriber 2] onNext: 0
        // [Subscriber 2] onNext: 1
        // [Subscriber 2] onNext: 2
        // 1.2초 sleep
        // cold publisher에 대해 새로운 subscription 발생
        // [Subscriber 3] onNext: 0
        // [Subscriber 3] onNext: 1
        // [Subscriber 3] onNext: 2
    }
}