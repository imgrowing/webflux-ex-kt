package study.wf

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class EtcExamplesTest {

    @Test
    fun `여러 행의 문장을 공백 단위로 나누기`() {
        val lines: String = """
            first line
            second line
            third line
            """.trimIndent()

        Mono.just(lines)
                .flatMapMany {
                    Flux.fromIterable(it.split("\n"))
                }
                .flatMap {
                    Flux.fromIterable(it.split(Regex("\\s+")))
                }
                .subscribe{ println(it) }
    }
}