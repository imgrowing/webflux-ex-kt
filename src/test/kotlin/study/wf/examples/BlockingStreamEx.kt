package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux

class BlockingStreamEx {

    @Test
    fun `toIterable - 스트림을 블로킹 구조로 변환한다, iterable로 바꾼다`() {
        val iterable = Flux
                .just(1, 2, 3)
                .toIterable()
        iterable.forEach { println(it) }
        /*
        1
        2
        3
         */
    }
}