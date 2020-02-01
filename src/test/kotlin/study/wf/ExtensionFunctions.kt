package study.wf

import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.UnicastProcessor

fun <T> Flux<T>.subscribeAndPrint(): Disposable = subscribe (
        {
            when (it) {
                is UnicastProcessor<*> -> it.subscribeAndPrint()
                is Flux<*> -> {
                    it
                            .collectList()
                            .subscribe {
                                list ->
                                println("        // onNext -> $list")
                            }
                }
                else -> println("        // onNext -> $it")
            }
        },
        { println("        // onError -> $it") },
        { println("        // onComplete") }
)

fun <T> Mono<T>.subscribeAndPrint(): Disposable = subscribe (
        { println("        // onNext -> $it") },
        { println("        // onError -> $it") },
        { println("        // onComplete") }
)

fun <T> UnicastProcessor<T>.subscribeAndPrint(): Disposable {
    print("        // onNext -> ")

    return subscribe (
            { print("$it, ") },
            { println("-> onError -> $it") },
            { println("-> onComplete") }
    )
}

