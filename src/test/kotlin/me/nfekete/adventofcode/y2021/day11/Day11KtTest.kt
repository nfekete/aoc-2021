package me.nfekete.adventofcode.y2021.day11

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class Day11KtTest {
    @Test
    fun test() {
        val grid = """
            11111
            19991
            19191
            19991
            11111
            """.trimIndent()
            .lines()
            .map { line -> line.map { char -> char.digitToInt() } }

        val it = grid.part1().iterator()

        Assertions.assertEquals(
            """
            11111
            19991
            19191
            19991
            11111
            """.trimIndent(), it.next().first.pretty()
        )

        Assertions.assertEquals(
            """
            34543
            40004
            50005
            40004
            34543
            """.trimIndent(), it.next().first.pretty()
        )

        Assertions.assertEquals(
            """
            45654
            51115
            61116
            51115
            45654
            """.trimIndent(), it.next().first.pretty()
        )
    }
}
