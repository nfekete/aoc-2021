package me.nfekete.adventofcode.y2021.day08

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class Day08KtTest {

    @Test
    fun testPart2() {
        val actual = TestCase("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf")
            .part2()
        Assertions.assertEquals(5353, actual)
    }

}

