package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import study.wf.subscribeAndPrint
import java.time.Duration.ofMillis

class ConcatMergeZipEx {

    @Test
    fun `concat - 여러 개의 스트림을 연결한다, 한 번에 하나의 스트림씩 읽어들여 합친다`() {
        Flux
                .concat(
                        Flux.just(1, 2, 3).delayElements(ofMillis(10)),
                        Flux.just(4, 5, 6).delayElements(ofMillis(10)),
                        Flux.just(7, 8, 9).delayElements(ofMillis(10))
                )
                .subscribeAndPrint()
        Thread.sleep(100)
        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onNext -> 4
        // onNext -> 5
        // onNext -> 6
        // onNext -> 7
        // onNext -> 8
        // onNext -> 9
        // onComplete
    }

    @Test
    fun `merge - 여러 개의 스트림을 연결한다, 동시에 여러 개의 스트림씩 읽어들여 합친다`() {
        Flux
                .merge(
                        Flux.just(1, 2, 3).delayElements(ofMillis(10)),
                        Flux.just(4, 5, 6).delayElements(ofMillis(10)),
                        Flux.just(7, 8, 9).delayElements(ofMillis(10))
                )
                .subscribeAndPrint()
        Thread.sleep(100)
        // onNext -> 1
        // onNext -> 4
        // onNext -> 7
        // onNext -> 5
        // onNext -> 2
        // onNext -> 8
        // onNext -> 6
        // onNext -> 3
        // onNext -> 9
        // onComplete
    }

    @Test
    fun `zip - 여러 개의 스트림을 하나로 압축한다, 여러 개의 스트림에서 각각 하나의 원소를 읽어와서 하나의 Tuple로 만들어 내보낸다`() {
        Flux
                .zip(
                        Flux.just(1, 2, 3).delayElements(ofMillis(10)),
                        Flux.just(4, 5, 6).delayElements(ofMillis(10)),
                        Flux.just(7, 8, 9).delayElements(ofMillis(10))
                )
                .subscribeAndPrint()
        Thread.sleep(100)
        // onNext -> [1,4,7]
        // onNext -> [2,5,8]
        // onNext -> [3,6,9]
        // onComplete
    }
}