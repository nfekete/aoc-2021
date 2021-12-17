package me.nfekete.adventofcode.y2021.day08

import me.nfekete.adventofcode.y2021.common.classpathFile

enum class Segment {
    a, b, c, d, e, f, g
}
typealias Digit = Set<Segment>

private fun String.toDigit() = map { Segment.valueOf("$it") }.toSet()
data class TestCase(
    val allDigits: Set<Digit>,
    val displayedDigigts: List<Digit>
)

fun TestCase(line: String) =
    line.split(" | ")
        .map { digitSequence ->
            digitSequence.split(" ").map { it.toDigit() }
        }.let { (left, right) -> TestCase(left.toSet(), right) }

private fun TestCase.one() = allDigits.single { digit -> digit.size == 2 }
private fun TestCase.seven() = allDigits.single { digit -> digit.size == 3 }
private fun TestCase.four() = allDigits.single { digit -> digit.size == 4 }
private fun TestCase.eight() = allDigits.single { digit -> digit.size == 7 }
private fun TestCase.part1(): Int {
    val one = one()
    val seven = seven()
    val four = four()
    val eight = eight()
    val unambiguousDigits = setOf(one, seven, four, eight)
    return displayedDigigts.filter { it in unambiguousDigits }.size
}

private fun TestCase.segmentFrequencies() = allDigits.flatten().groupingBy { it }.eachCount()
private fun Map<Segment, Int>.whereCountIs(count: Int) = filter { it.value == count }

fun TestCase.part2(): Int {
    val one = one()
    val seven = seven()
    val four = four()
    val eight = eight()

    val segmentFrequencies = segmentFrequencies()

    // 9 -> f
    // 8 -> a, c
    // 7 -> d, g
    // 6 -> b
    // 5 -> e

    val a = (seven - one).single()
    val b = segmentFrequencies.whereCountIs(6).keys.single()
    val f = segmentFrequencies.whereCountIs(9).keys.single()
    val c = (one - f).single()
    val e = segmentFrequencies.whereCountIs(4).keys.single()
    val dg = segmentFrequencies.whereCountIs(7).keys
    val g = (dg - four).single()
    val d = (dg - g).single()

    val zero = setOf(a, b, c, e, f, g)
    val two = setOf(a, c, d, e, g)
    val three = setOf(a, c, d, f, g)
    val five = setOf(a, b, d, f, g)
    val six = setOf(a, b, d, e, f, g)
    val nine = setOf(a, b, c, d, f, g)


    val digitMapping = listOf(zero, one, two, three, four, five, six, seven, eight, nine)
        .withIndex()
        .associate { (index, value) -> value to index }

    return displayedDigigts
        .map { digitMapping[it]!! }
        .fold(0) { acc, digit -> acc * 10 + digit }
}


private fun main() {
    val input = classpathFile("day08/input.txt")
        .readLines()
        .map(::TestCase)

    input.sumOf { it.part1() }.let { println("Part1: $it") }
    input.sumOf { it.part2() }.let { println("Part2: $it") }

}
