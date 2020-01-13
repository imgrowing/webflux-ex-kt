package study.wf

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

var numberCount = 0

fun getResult(number: Int): Mono<String> {
    return Mono
            .defer {
                Mono
                        .fromSupplier {
                            if (number == 5 && numberCount <= 0) {
                                numberCount += 1
                                throw RuntimeException("num error")
                            }
                            "number result $number"
                        }
                        .doOnError { println("doOnError: ${it.message}") }
                        .retry(1) // Mono.fromSupplier { ... } 로 감싼 내부 대해서만 retry가 수행됨. defer 와는 무관
            }
}

var timeout5Count = 0
var timeout10Count = 0

fun getTimeoutResult(number: Int): Mono<String> {
    return Mono
            .defer {
                Mono
                        .fromSupplier {
                            if (number == 5 && timeout5Count <= 0) {
                                timeout5Count += 1
                                Thread.sleep(200)
                            }
                            if (number == 10 && timeout10Count <= 0) {
                                timeout10Count += 1
                                Thread.sleep(200)
                            }
                            "timeout result $number"
                        }
                        .timeout(Duration.ofMillis(100))
                        .doOnError { println("doOnError: ${it.message}") }
                        .retry(1) // Mono.fromSupplier { ... } 로 감싼 내부 범위에서 대해서만 retry가 수행됨. defer 와는 무관
            }
}

fun main(args: Array<String>) {
    Flux
            .range(1, 10)
            .flatMap { getResult(it) }
            .subscribe { println(it) }
/*
number result 1
number result 2
number result 3
number result 4
doOnError: num error
number result 5
number result 6
number result 7
number result 8
number result 9
number result 10
 */

    println("=====================")

    Flux
            .range(1, 10)
            .flatMap { getTimeoutResult(it) }
            .subscribe { println(it) }
/*
timeout result 1
timeout result 2
timeout result 3
timeout result 4
doOnError: Did not observe any item or terminal signal within 100ms in 'source(MonoSupplier)' (and no fallback has been configured)
timeout result 5
timeout result 6
timeout result 7
timeout result 8
timeout result 9
doOnError: Did not observe any item or terminal signal within 100ms in 'source(MonoSupplier)' (and no fallback has been configured)
timeout result 10
 */

    Thread.sleep(500)
}