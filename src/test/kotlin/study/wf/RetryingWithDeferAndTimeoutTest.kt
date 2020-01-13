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
                        .retry(1) // defer로 감싼 내부 범위에서만 retry가 수행됨
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
                        .retry(1) // defer로 감싼 내부 범위에서만 retry가 수행됨
            }
}

fun main(args: Array<String>) {
    Flux
            .range(1, 10)
            .flatMap { getResult(it) }
            .subscribe { println(it) }

    println("=====================")

    Flux
            .range(1, 10)
            .flatMap { getTimeoutResult(it) }
            .subscribe { println(it) }

    Thread.sleep(500)
}