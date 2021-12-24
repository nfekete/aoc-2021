package me.nfekete.adventofcode.y2021.day18

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ReducerTest {

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = [
            "[[[[[9,8],1],2],3],4]                  |   [[[[0,9],2],3],4]",
            "[7,[6,[5,[4,[3,2]]]]]                  |   [7,[6,[5,[7,0]]]]",
            "[[6,[5,[4,[3,2]]]],1]                  |   [[6,[5,[7,0]]],3]",
            "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]  |   [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]",
            "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]      |   [[3,[2,[8,0]]],[9,[5,[7,0]]]]",
        ]
    )
    fun explode(before: String, after: String) {
        assertEquals(after, Number.parse(before).let { ExplosionReducer().reduce(it) }.toString())
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = [
            "[[[[0,7],4],[15,[0,13]]],[1,1]]     |  [[[[0,7],4],[[7,8],[0,13]]],[1,1]]",
            "[[[[0,7],4],[[7,8],[0,13]]],[1,1]]  |  [[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]"
        ]
    )
    fun split(before: String, after: String) {
        assertEquals(after, Number.parse(before).let { SplitReducer().reduce(it) }.toString())
    }

    @Test
    fun combined() {
        val result = Number.parse("[[[[4,3],4],4],[7,[[8,4],9]]]") + Number.parse("[1,1]")
        assertEquals(Number.parse("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"), result)
    }
}
