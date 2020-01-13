package study.wf

import reactor.core.publisher.Flux
import java.time.Duration

fun main(args: Array<String>) {
    val flux: Flux<String> = Flux.interval(Duration.ofMillis(250))
            .map { input ->
                if (input < 3) "tick " + input
                else throw RuntimeException("boom")
            }
            .onErrorReturn("Uh oh")

    flux.subscribe { println(it) }
    Thread.sleep(2100)
}
