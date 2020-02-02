package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.SynchronousSink
import study.wf.subscribeAndPrint
import java.util.stream.IntStream

class StreamFactoryEx {

    @Test
    fun `push - 시퀀스를 프로그래밍 방식으로 생성한다, 싱글 스레드에서 publish`() {
        Flux
                .push<Int> { emitter ->
                    IntStream.range(101, 105)
                            .forEach { emitter.next(it) } // FluxSink<T> next(T t) : t - the value to emit
                }
                .subscribeAndPrint()
        // onNext -> 101
        // onNext -> 102
        // onNext -> 103
        // onNext -> 104
    }

    @Test
    fun `create - 시퀀스를 프로그래밍 방식으로 생성한다, 멀티 스레드에서 publish 가능`() {
        Flux
                .create<Int> { emitter ->
                    IntStream.range(101, 105)
                            .forEach { emitter.next(it) } // FluxSink<T> next(T t) : t - the value to emit
                }
                .subscribeAndPrint()
        // onNext -> 101
        // onNext -> 102
        // onNext -> 103
        // onNext -> 104
    }

    @Test
    fun `generate - 시퀀스를 프로그래밍 방식으로 생성한다, 내부적인 상태를 기반으로 생성할 수 있다`() {
        Flux
                .generate( // scan operator와 비슷한 느낌이다
                        { mutableListOf<Int>() },  // 초기값. numbers 로 전달됨
                        { numbers, sink: SynchronousSink<List<Int>> ->  // generator 함수
                            val n =  if (numbers.isNotEmpty()) numbers.last() else 0
                            numbers.add(n + 1)
                            sink.next(numbers) // onNext로 emit 한다
                            numbers // 반환값은 generator 함수의 numbers 인자로 전달됨
                        }
                )
                .take(5)
                .subscribeAndPrint()
        // onNext -> [1]
        // onNext -> [1, 2]
        // onNext -> [1, 2, 3]
        // onNext -> [1, 2, 3, 4]
        // onNext -> [1, 2, 3, 4, 5]
        // onComplete
    }

}