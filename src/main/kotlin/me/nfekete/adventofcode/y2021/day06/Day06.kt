package me.nfekete.adventofcode.y2021.day06

import me.nfekete.adventofcode.y2021.common.classpathFile

private const val reproCycle = 7
private const val newOffspringReproCycle = reproCycle + 2
private fun Int.lanternFishOffsprings(days: Int = 80): Long =
    if (days <=this)
        1
    else
        lanternFishOffsprings(days - reproCycle) + lanternFishOffsprings(days - newOffspringReproCycle)

private fun main() {
    val input = classpathFile("day06/input.txt")
        .readLine()
        .split(",")
        .map { it.toInt() }

    input
        .map { it.lanternFishOffsprings() }
        .sum()
        .let { println("Part1: $it") }

}
