package study.wf

import reactor.core.Disposable
import reactor.core.publisher.Flux

fun <T> Flux<T>.subscribeAndPrint(): Disposable = subscribe (
        { println("onNext -> $it") },
        { println("onError -> $it")},
        { println("onComplete") }
)
