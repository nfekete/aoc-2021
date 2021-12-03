package me.nfekete.adventofcode.y2021.day03

import me.nfekete.adventofcode.y2021.common.classpathFile

private fun Char.isOne() = this == '1'

private fun main() {
    val input = classpathFile("day03/input.txt")
        .readLines()
    part1(input)
    part2(input)
}

private fun part1(input: List<String>) {
    val indices = input.first().indices
    val mostCommonBits = indices.map { index ->
        mostCommonBit(input, index)
    }
    val gammaRate = mostCommonBits.toBinary()
    val epsilonRate = mostCommonBits.invertBits().toBinary()
    println("Part: $gammaRate * $epsilonRate = ${gammaRate * epsilonRate}")
}

private fun mostCommonBit(input: List<String>, index: Int): Int {
    val isOne = input.map { it[index] }.count { it.isOne() } >= (input.size + 1) / 2
    return if (isOne) 1 else 0
}

private fun List<String>.filterByBitCriteria(index: Int, criteria: (Int) -> Int = { it }) =
    if (size == 1) {
        this
    } else {
        val mostCommonBit = criteria(mostCommonBit(this, index))
        filter { it[index] == mostCommonBit.digitToChar(2) }
    }

private fun part2(input: List<String>) {
    val oxygenGeneratorRating = input.first().indices.fold(input) { list, index ->
        list.filterByBitCriteria(index)
    }.single().toInt(2)
    val co2ScrubberRating = input.first().indices.fold(input) { list, index ->
        list.filterByBitCriteria(index) { 1 - it }
    }.single().toInt(2)

    println("Part: $oxygenGeneratorRating * $co2ScrubberRating = ${oxygenGeneratorRating * co2ScrubberRating}")
}

private fun List<Int>.toBinary(): Int = fold(0) { current, bit -> current shl 1 or bit }
private fun List<Int>.invertBits() = map { if (it == 1) 0 else 1 }
