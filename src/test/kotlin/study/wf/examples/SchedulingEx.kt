package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.util.Loggers

class SchedulingEx {

    private val log = Loggers.getLogger(this.javaClass)

    @Test
    fun `publishOn - 런타임 실행을 처리할 워커를 지정한다, 전용 워커가 처리할 수 있는 큐에 원소를 담는다`() {
        Flux
                .range(0, 3)
                .map {
                    val c = 'a' + it
                    log.info("toChar : $c")
                    c
                }
                .filter {
                    log.info("filterChar : $it")
                    it in listOf('a', 'c', 'e')
                }

                .publishOn(Schedulers.elastic())

                .map {
                    log.info("charToString : $it")
                    it.toString()
                }
                .map {
                    log.info("makeHello : $it")
                    "Hello User $it"
                }
                .log()
                .subscribe()

        /*
        흐름
        [main thread   ] toChar ==> filterChar ==> publishOn: Queue에 담는다
        ------------------------ (상/하는 별도로 병렬 진행됨)
        [elastic thread] Queue ==> charToString ==> makeHello
         */


        // [main]      - onSubscribe([Fuseable] FluxMapFuseable.MapFuseableSubscriber)
        // [main]      - request(unbounded)

        // main 스레드에서는 a -> b -> c 의 순서가 지켜지며 처리된다.
        // 전용 워커(elastic) 스레드에서는 a -> c 의 순서가 지켜지며 처리된다.

        // [main]      - toChar : a
        // [main]      - filterChar : a
        // [main]      - toChar : b
        // [main]      - filterChar : b
        // [main]      - toChar : c
        // [elastic-2] - charToString : a     ==> a: Queue에서 전용 워커가 하나씩 꺼내서 수행
        // [main]      - filterChar : c       ==> main thread와 전용 워커의 처리가 병렬로 되고 있다
        // [elastic-2] - makeHello : a        ==> a: 전용 워커가 수행
        // [elastic-2] - onNext(Hello User a) ==> a: onNext도 전용 워커가 수행
        // [elastic-2] - charToString : c     ==> c: Queue에서 전용 워커가 하나씩 꺼내서 수행
        // [elastic-2] - makeHello : c        ==> c: 전용 워커가 수행
        // [elastic-2] - onNext(Hello User c) ==> c: onNext도 전용 워커가 수행
        // [elastic-2] - onComplete()
    }

    @Test
    fun `subscribeOn - request()는 main thread에서 처리되고, 그 이후 구독 체인의 모든 작업이 전용 워커에서 실행된다`() {
        Flux
                .range(0, 3)
                .map {
                    val c = 'a' + it
                    log.info("toChar : $c")
                    c
                }
                .filter {
                    log.info("filterChar : $it")
                    it in listOf('a', 'c', 'e')
                }

                .map {
                    log.info("charToString : $it")
                    it.toString()
                }
                .map {
                    log.info("makeHello : $it")
                    "Hello User $it"
                }
                .subscribeOn(Schedulers.elastic())
                .log()
                .subscribe()

        Thread.sleep(100)

        // 전용 워커(elastic) 스레드에서는 a -> b -> c 의 순서가 지켜지며 처리된다.

        // [main]      - onSubscribe(FluxMap.MapSubscriber)
        // [main]      - request(unbounded)    ==> request()는 main thread 에서 실행된다
        // [elastic-2] - toChar : a            ==> 구독 체인의 모든 작업이 전용 워커에서 실행된다.
        // [elastic-2] - filterChar : a
        // [elastic-2] - charToString : a
        // [elastic-2] - makeHello : a
        // [elastic-2] - onNext(Hello User a)
        // [elastic-2] - toChar : b
        // [elastic-2] - filterChar : b
        // [elastic-2] - toChar : c
        // [elastic-2] - filterChar : c
        // [elastic-2] - charToString : c
        // [elastic-2] - makeHello : c
        // [elastic-2] - onNext(Hello User c)
        // [elastic-2] - onComplete()
    }
}