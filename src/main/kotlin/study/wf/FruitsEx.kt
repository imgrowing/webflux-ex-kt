package study.wf

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    val basket1 = mutableListOf("kiwi", "orange", "lemon", "orange", "lemon", "kiwi")
    val basket2 = mutableListOf("banana", "lemon", "lemon", "kiwi")
    val basket3 = mutableListOf("strawberry", "orange", "lemon", "grape", "strawberry")

    val baskets = mutableListOf(basket1, basket2, basket3)
    val basketFlux = Flux.fromIterable(baskets)

    val countDownLatch = CountDownLatch(1)

    // basketFlux 로부터 중복 없이 각 과일 종류를 나열하고, 각 과일이 몇 개씩 들어있는지 각 바구니마다 출력하는 코드를 작성해보도록 하겠습니다.
    basketFlux.flatMap { basket ->
        val distinctFruits = Flux.fromIterable(basket).distinct().collectList()
                .subscribeOn(Schedulers.parallel())

        val fruitByCount = Flux.fromIterable(basket)
                .groupBy { it }
                .flatMap { groupedFlux ->
                    groupedFlux.count()
                            .map { count ->
                                Pair(groupedFlux.key()!!, count!!)
                            }
                }
                .reduce(mutableMapOf<String, Long>(),
                        { m, p ->
                            m[p.first] = p.second
                            m
                        }
                ).subscribeOn(Schedulers.parallel())

        Mono.zip(distinctFruits, fruitByCount)
                .map { tuple ->
                    var s = "items = $basket, "
                    tuple.t1.forEach { fruit ->
                        s += "$fruit: ${tuple.t2[fruit]} "
                    }
                    println(s)
                }
    }.subscribe { countDownLatch.countDown() }

    countDownLatch.await(2, TimeUnit.SECONDS)

}
