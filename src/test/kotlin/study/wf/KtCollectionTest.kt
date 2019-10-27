package study.wf

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test


class KtCollectionTest {


    @Test
    fun `list-substract`() {
        val items_123 = listOf(1, 2, 3)
        val items_34 = listOf(3, 4)


        assertThat(items_123.subtract(items_34), equalTo(setOf(1, 2)))
        assertThat(items_123.subtract(items_34), equalTo(setOf(2, 1)))


        assertThat(items_123.intersect(items_34), equalTo(setOf(3)))


        assertThat(items_123.minus(items_34), equalTo(listOf(1, 2)))
        assertThat(items_123.minus(items_34), not(equalTo(listOf(2, 1))))


        assertThat(items_123.plus(items_34), equalTo(listOf(1, 2, 3, 3, 4)))
        assertThat(items_123.plus(items_34), not(equalTo(listOf(2, 3, 3, 4, 1))))


        assertThat(items_123.union(items_34), equalTo(setOf(1, 2, 3, 4)))
        assertThat(items_123.union(items_34), equalTo(setOf(2, 3, 4, 1)))
    }
}