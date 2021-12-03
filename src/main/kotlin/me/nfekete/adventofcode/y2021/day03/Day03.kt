package me.nfekete.adventofcode.y2021.day03

import me.nfekete.adventofcode.y2021.common.classpathFile

private fun Char.isOne() = this == '1'

private fun main() {
    val input = classpathFile("day03/input.txt")
        .readLines()
    part1(input)


}

private fun part1(input: List<String>) {
    val indices = input.first().indices
    val mostCommonBits = indices.map { index ->
        val isOne = input.map { it[index] }.count { it.isOne() } > input.size / 2
        if (isOne)
            1
        else
            0
    }
    val gammaRate = mostCommonBits.toBinary()
    val epsilonRate = mostCommonBits.invertBits().toBinary()
    println("Part: $gammaRate * $epsilonRate = ${gammaRate * epsilonRate}")
}

private fun List<Int>.toBinary(): Int = fold(0) { current, bit -> current shl 1 or bit }
private fun List<Int>.invertBits() = map { if (it == 1) 0 else 1 }
