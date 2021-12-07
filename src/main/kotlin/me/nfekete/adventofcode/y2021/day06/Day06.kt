package me.nfekete.adventofcode.y2021.day06

import me.nfekete.adventofcode.y2021.common.classpathFile
import me.nfekete.adventofcode.y2021.common.memoized

private const val reproCycle = 7
private const val newOffspringReproCycle = reproCycle + 2

private val lanternFishOffspringsM = { timer: Int, days: Int ->
    if (days <= timer)
        1
    else
        timer.lanternFishOffsprings(days - reproCycle) + timer.lanternFishOffsprings(days - newOffspringReproCycle)
}.memoized()
private fun Int.lanternFishOffsprings(days: Int): Long = lanternFishOffspringsM(this, days)

private fun main() {
    val input = classpathFile("day06/input.txt")
        .readLine()
        .split(",")
        .map { it.toInt() }

    input
        .map { it.lanternFishOffsprings(80) }
        .sum()
        .let { println("Part1: $it") }

    input
        .map { it.lanternFishOffsprings(256) }
        .sum()
        .let { println("Part2: $it") }
}

