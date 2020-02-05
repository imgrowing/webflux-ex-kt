package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import study.wf.subscribeAndPrint
import java.util.*

class BadInputException(errorMessage: String = "Bad Input") : RuntimeException(errorMessage)


class ErrorHandlingEx {

    @Test
    fun `onErrorReturn, fallbackValue - 예외 발생시 사전에 정의된 정적 값으로 대체할 수 있다`() {
        Flux
                .range(1, 3)
                .map { i -> if (i < 3) i else (3 / 0) }
                .onErrorReturn(-1)
                .subscribeAndPrint()

        // onNext -> 1
        // onNext -> 2
        // onNext -> -1
        // onComplete
    }

    @Test
    fun `onErrorReturn, 1st Predicate, 2nd fallbackValue - 예외 발생시 예외로 계산된 값으로 대체할 수 있다`() {
        Flux
                .range(1, 3)
                .map { i -> if (i < 3) i else (3 / 0) }
                .onErrorReturn({ e -> e is ArithmeticException}, -1)
                .subscribeAndPrint()

        // onNext -> 1
        // onNext -> 2
        // onNext -> -1
        // onComplete
    }

    @Test
    fun `onErrorResume - 예외 발생시 대체 워크플로우를 실행한다`() {
        Flux
                .range(1, 3)
                .map { i -> if (i < 3) i else (3 / 0) }
                .onErrorResume { Mono.just(-99) }
                .subscribeAndPrint()

        // onNext -> 1
        // onNext -> 2
        // onNext -> -99
        // onComplete
    }

    @Test
    fun `onErrorResume, by exception - 예외 발생시 대체 워크플로우를 실행한다`() {
        val random = Random()
        Flux
                .range(0, 2)
                .map {
                    val nextInt = random.nextInt(3)
                    println("        // nextInt -> $nextInt")
                    when (nextInt) {
                        0 -> 0
                        1 -> throw IllegalStateException("invalid state")
                        2 -> throw IllegalArgumentException("invalid number")
                        else -> 0
                    }
                }
                .onErrorResume {
                    println("        // onErrorResume -> $it")
                    when (it) {
                        is IllegalStateException -> Flux.just(11, 12)
                        is IllegalArgumentException -> Flux.just(21, 22)
                        else -> Mono.empty()
                    }
                }
                .subscribeAndPrint()

        // 정상 흐름 : 0, 0 이 나온 경우 -> 0, 0이 만들어짐
        // nextInt -> 0
        // onNext -> 0
        // nextInt -> 0
        // onNext -> 0
        // onComplete

        // 에러 흐름 : 첫 번째에 1이 나온 경우 -> 11, 12가 만들어짐
        // nextInt -> 1
        // onErrorResume -> java.lang.IllegalStateException: invalid state
        // onNext -> 11
        // onNext -> 12
        // onComplete

        // 에러 흐름 : 0, 2가 나온 경우 -> 21, 22가 만들어짐
        // nextInt -> 0
        // onNext -> 0
        // nextInt -> 2
        // onErrorResume -> java.lang.IllegalArgumentException: invalid number
        // onNext -> 21
        // onNext -> 22
        // onComplete
    }

    @Test
    fun `onErrorMap - 발생한 예외를 잡아, 상황을 더 잘 나타내는 다른 예외로 변환할 수 있다`() {
        Flux
                .range(1, 3)
                .map { i -> if (i < 3) i else (3 / 0) }
                .onErrorMap {
                    println("original exception : $it")
                    BadInputException("올바르지 않은 입력입니다")
                }
                .subscribeAndPrint()

        // onNext -> 1
        // onNext -> 2
        // original exception : java.lang.ArithmeticException: / by zero
        // onError -> study.wf.examples.BadInputException: 올바르지 않은 입력입니다
    }

    @Test
    fun `retry - 예외 발생시 스트림을 "무한히" 다시 구독한다`() {
        val random = Random()
        Flux
                .just(0)
                .flatMap {
                    val nextInt = random.nextInt(3)
                    println("        // nextInt : $nextInt")
                    if (nextInt == 0) {
                        println("        // IllegalArgumentException occurred($nextInt)")
                        throw IllegalArgumentException("invalid number")
                    }
                    else Flux.range(1, nextInt)
                }
                .retry()
                .subscribeAndPrint()

        // 난수 1이 나와서 정상적으로 진행된 경우
        // nextInt : 1
        // onNext -> 1
        // onComplete

        // 난수 2가 나와서 정상적으로 진행된 경우
        // nextInt : 2
        // onNext -> 1
        // onNext -> 2
        // onComplete

        // 난수 0이 나와서 재시도 후 정상적으로 진행된 경우
        // nextInt : 0
        // IllegalArgumentException occurred(0)
        // nextInt : 2
        // onNext -> 1
        // onNext -> 2
        // onComplete

        // 난수 0이 나와서 재시도 -> 난수 0이 나와서 재시도 후 정상적으로 진행된 경우
        // nextInt : 0
        // IllegalArgumentException occurred(0)
        // nextInt : 0
        // IllegalArgumentException occurred(0)
        // nextInt : 2
        // onNext -> 1
        // onNext -> 2
        // onComplete
    }

    @Test
    fun `retry - 예외 발생시 스트림을 "1회" 다시 구독한다, 따라서 최대 2회 구독될 수 있다`() {
        val random = Random()
        Flux
                .just(0)
                .flatMap {
                    val nextInt = random.nextInt(3)
                    println("        // nextInt : $nextInt")
                    if (nextInt == 0) {
                        println("        // IllegalArgumentException occurred($nextInt)")
                        throw IllegalArgumentException("invalid number")
                    }
                    else Flux.range(1, nextInt)
                }
                .retry(1) // 최초 구독 + 1회 더 구독
                .subscribeAndPrint()

        // 난수 1이 나와서 정상적으로 진행된 경우
        // nextInt : 1
        // onNext -> 1
        // onComplete

        // 난수 2가 나와서 정상적으로 진행된 경우
        // nextInt : 2
        // onNext -> 1
        // onNext -> 2
        // onComplete

        // 난수 0이 나와서 재시도 -> 난수 1이 나와서 정상적으로 진행된 경우
        // nextInt : 0
        // IllegalArgumentException occurred(0)
        // nextInt : 1
        // onNext -> 1
        // onComplete

        // 난수 0이 나와서 재시도 -> 난수 0이 나와서 에러로 진행된 경우
        // nextInt : 0
        // IllegalArgumentException occurred(0)
        // nextInt : 0
        // IllegalArgumentException occurred(0)
        // onError -> java.lang.IllegalArgumentException: invalid number
    }
}

