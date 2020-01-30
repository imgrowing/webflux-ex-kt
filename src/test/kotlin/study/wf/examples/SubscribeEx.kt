package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux

class SubscribeEx {
    @Test
    fun `subscribe - onNextConsumer`() {
        Flux
                .range(1, 3)
                .subscribe {
                    v -> println(v)
                }
    }

    @Test
    fun `subscribe - onNextConsumer, onErrorConsumer`() {
        Flux
                .range(1, 3)
                .map { i -> if (i < 3) i else (3 / 0) }
                .subscribe(
                        { println("onNext : $it") },
                        { error -> println("onError : $error") }
                )
    }

    @Test
    fun `subscribe - onNextConsumer, onErrorConsumer, onCompleteConsumer`() {
        Flux
                .range(1, 3)
                .map { i -> if (i < 3) i else (3 / 0) }
                .subscribe(
                        { println("onNext : $it") },
                        { error -> println("onError : $error") },
                        { println("onComplet") }
                )

        Flux
                .range(1, 3)
                .subscribe(
                        { println("onNext : $it") },
                        { error -> println("onError : $error") },
                        { println("onComplet") }
                )
    }
}