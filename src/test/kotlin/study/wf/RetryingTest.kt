package study.wf

import reactor.core.publisher.Flux
import java.lang.System.err
import java.time.Duration

fun main(args: Array<String>) {
    Flux.interval(Duration.ofMillis(250))
            .map { input ->
                if (input < 3) "tick " + input
                else throw RuntimeException("boom")
            }
            .retry(1)
            .elapsed() // map() 함수로 동작한다. Tuple<Long /* elapsed time ms */, T /* input */> 으로 mapping 한다.
            .subscribe(::println, err::println)

    Thread.sleep(2100)
    /*
    [256,tick 0]
    [248,tick 1]
    [251,tick 2]
                 <= 여기서 RuntimeException 이 발생했고, retry 1회가 수행됨
    [512,tick 0] <= 새로운 시퀀스가 시작됨. 그래서 "tick 0" 부터 시작함
    [249,tick 1]
    [248,tick 2]
    java.lang.RuntimeException: boom <= 여기서 RuntimeException 이 발생했고, retry를 더 이상 하지 않음
     */
}