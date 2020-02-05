package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import java.time.Duration

class TimeEx {

    @Test
    fun `interval - 주기적으로 이벤트를 생성한다`() {
        Flux
                .interval(Duration.ofMillis(100))
                .take(3)
                .log()
                .subscribe()
        Thread.sleep(500)
        
        // 19:32:29.535 [main]       - onSubscribe(FluxInterval.IntervalRunnable)
        // 19:32:29.537 [main]       - request(unbounded)
        // 19:32:29.641 [parallel-1] - onNext(0)  ==> request 후 100ms 가 지난 시점에 onNext()로 이벤트가 발생한다
        // 19:32:29.742 [parallel-1] - onNext(1)  ==> 100ms 간격
        // 19:32:29.842 [parallel-1] - onNext(2)
        // 19:32:29.844 [parallel-1] - cancel()
    }

    @Test
    fun `delayElements - 이벤트를 지정된 시간 간격만큼 지연시킨다`() {
        Flux
                .just(1, 2, 3)
                .delayElements(Duration.ofMillis(100))
                .log()
                .subscribe()
        Thread.sleep(500)

        // 19:40:18.869 [main]       - onSubscribe(FluxTake.TakeSubscriber)
        // 19:40:18.871 [main]       - request(unbounded)
        // 19:40:19.009 [parallel-1] - onNext(1)  ==> request() 후 약 100ms 이후에 onNext 발생
        // 19:40:19.110 [parallel-2] - onNext(2)  ==> 직전 onNext 후 약 100ms 이후에 onNext 발생
        // 19:40:19.211 [parallel-3] - onNext(3)  ==> 직전 onNext 후 약 100ms 이후에 onNext 발생
        // 19:40:19.211 [parallel-3] - onComplete()
    }

    @Test
    fun `delaySequence - 시퀀스 자체를 지정된 시간 간격만큼 지연시킨다`() {
        Flux
                .just(1, 2, 3)
                .delaySequence(Duration.ofMillis(100))
                .log()
                .subscribe()
        Thread.sleep(500)

        // 19:45:24.315 [main]       - onSubscribe(SerializedSubscriber)
        // 19:45:24.317 [main]       - request(unbounded)
        // 19:45:24.421 [parallel-1] - onNext(1)  ==> request() 후 약 100ms 이후에 onNext 발생
        // 19:45:24.421 [parallel-1] - onNext(2)  ==> 이벤트 사이의 지연은 발생하지 않는다
        // 19:45:24.421 [parallel-1] - onNext(3)
        // 19:45:24.421 [parallel-1] - onComplete()
    }

    @Test
    fun `elapsed - 이전 이벤트와의 시간 간격을 측정한다`() {
        Flux
                .just(1, 2, 3)
                .delayElements(Duration.ofMillis(100))
                .elapsed()
                .log()
                .subscribe()
        Thread.sleep(500)

        // 19:58:02.653 [main]       | onSubscribe([Fuseable] FluxElapsed.ElapsedSubscriber)
        // 19:58:02.656 [main]       | request(unbounded)
        // 19:58:02.834 [parallel-1] | onNext([181,1])  ==> request()와 181ms 차이
        // 19:58:02.938 [parallel-2] | onNext([105,2])  ==> 이전 onNext()와 105ms 차이
        // 19:58:03.042 [parallel-3] | onNext([104,3])  ==> 이전 onNext()와 104ms 차이
        // 19:58:03.043 [parallel-3] | onComplete()
    }

}