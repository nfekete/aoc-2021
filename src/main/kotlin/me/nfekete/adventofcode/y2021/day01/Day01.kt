package me.nfekete.adventofcode.y2021.day01

import me.nfekete.adventofcode.y2021.common.classpathFile

private fun main() {
    val depths = classpathFile("day01/input.txt").readLines().map { it.toInt() }
    depths
        .windowed(2, 1, false)
        .count { (a, b) -> a < b }
        .let { count -> println(count) }
    depths
        .windowed(3, 1, false)
        .map { it.sum() }
        .windowed(2, 1, false)
        .count { (a, b) -> a < b }
        .let { count -> println(count) }
}
