package study.wf.creating_new_sequence

import reactor.core.publisher.Flux

fun main(args: Array<String>) {
    Flux
            .just("1")
            .subscribe { println(it) }
}