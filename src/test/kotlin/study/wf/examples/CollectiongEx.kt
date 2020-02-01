package study.wf.examples

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import study.wf.subscribeAndPrint

class CollectiongEx {

    @Test
    fun `collectList - 모든 원소를 수집한다`() {
        val collectListMono = Flux
                .just(1, 2, 3, 4, 5)
                .collectList();
        collectListMono.subscribeAndPrint()

        // onNext -> [1, 2, 3, 4, 5]
        // onComplete
    }

    @Test
    fun `collectSortedList - 모든 원소를 정렬된 상태로 수집한다`() {
        Flux
                .just(3, 5, 2, 1, 4)
                .collectSortedList()
                .subscribeAndPrint()

        // onNext -> [1, 2, 3, 4, 5]
        // onComplete
    }

    @Test
    fun `collectSortedList - 정렬방식 지정, 모든 원소를 정렬된 상태로 수집한다`() {
        Flux
                .just(3, 5, 2, 1, 4)
                .collectSortedList(Comparator.reverseOrder())
                .subscribeAndPrint()

        // onNext -> [5, 4, 3, 2, 1]
        // onComplete
    }

    @Test
    fun `collectSortedList - 정렬방식 지정_임의, 모든 원소를 임의로 정렬된 상태로 수집한다 ~ pair의 first는 숫자 크기순, second는 문자열 역순`() {
        Flux
                .just(Pair(1, "A"), Pair(1, "C"), Pair(2, "D"), Pair(1, "B"), Pair(2, "A"))
                .collectSortedList { p1, p2 ->
                    if (p1.first != p2.first)
                        p1.first.compareTo(p2.first)
                    else
                        p1.second.compareTo(p2.second) * -1 // 역순

                }
                .subscribeAndPrint()

        // onNext -> [(1, C), (1, B), (1, A), (2, D), (2, A)]
        // onComplete
    }

    @Test
    fun `collectMap - map으로 변환, key가 중복되면 가장 최근에 들어온 value가 적용된다`() {
        Flux
                .just("a", "c", "b", "a", "b")
                .collectMap(
                        { s -> s },
                        { s -> s.first().toInt() }
                )
                .subscribeAndPrint()

        // onNext -> {a=97, b=98, c=99}
        // onComplete
    }

    @Test
    fun `collectMultiMap - map으로 변환, value는 Collection 으로 수집된다`() {
        Flux
                .just("a", "c", "b", "a", "b")
                .collectMultimap(
                        { s -> s },
                        { s -> s.first().toInt() }
                )
                .subscribeAndPrint()

        // onNext -> {a=[97, 97], b=[98, 98], c=[99]}
        // onComplete
    }

    @Test
    fun `distinct - 중복을 제거한다, 모든 원소를 대상으로 연산하므로 원소의 개수라 많은 경우에는 주의해야 한다`() {
        Flux
                .just(1, 2, 3, 1, 2, 5, 3)
                .distinct()
                .subscribeAndPrint();

        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onNext -> 5
        // onComplete
    }

    @Test
    fun `distinctUntilChanged - 연속되는 중복을 제거한다`() {
        Flux
                .just(1, 2, 2, 3, 3, 1, 2, 3, 3, 4)
                .distinctUntilChanged()
                .subscribeAndPrint();

        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onNext -> 1
        // onNext -> 2
        // onNext -> 3
        // onNext -> 4
        // onComplete
    }
}