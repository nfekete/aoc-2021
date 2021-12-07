package me.nfekete.adventofcode.y2021.day07

import me.nfekete.adventofcode.y2021.common.classpathFile
import kotlin.math.abs

private fun sumIntegersUpTo(n: Int) = n * (n+1) / 2

private fun main() {
    val input = classpathFile("day07/input.txt")
        .readLine()
        .split(",")
        .map { it.toInt() }

    input.indices.map { index ->
        input.sumOf { position -> abs(position - index) }
    }.minOrNull().let { println("Part1: $it") }

    input.indices.map { index ->
        input.sumOf { position -> sumIntegersUpTo(abs(position - index)) }
    }.minOrNull().let { println("Part2: $it") }
}
