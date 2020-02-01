package study.wf

import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun <T> Flux<T>.subscribeAndPrint(): Disposable = subscribe (
        { println("        // onNext -> $it") },
        { println("        // onError -> $it")},
        { println("        // onComplete") }
)

fun <T> Mono<T>.subscribeAndPrint(): Disposable = subscribe (
        { println("        // onNext -> $it") },
        { println("        // onError -> $it")},
        { println("        // onComplete") }
)
