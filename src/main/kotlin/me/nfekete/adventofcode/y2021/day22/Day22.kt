package me.nfekete.adventofcode.y2021.day22

import me.nfekete.adventofcode.y2021.common.classpathFile

data class Step(val on: Boolean, val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    companion object

    fun apply(set: Set<Coord>) =
        xRange.flatMap { x ->
            yRange.flatMap { y ->
                zRange.map { z -> Coord(x, y, z) }
            }
        }.let {
            if (on) {
                set + it
            } else {
                set - it
            }
        }
}

val stepRegex = Regex("(on|off) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")
fun Step.Companion.parse(string: String) =
    stepRegex.matchEntire(string)!!.destructured.let { (command, x1, x2, y1, y2, z1, z2) ->
        Step(command == "on", x1.toInt()..x2.toInt(), y1.toInt()..y2.toInt(), z1.toInt()..z2.toInt())
    }

data class Coord(val x: Int, val y: Int, val z: Int)

fun main() {
    val steps = classpathFile("day22/input.txt")
        .readLines()
        .map { Step.parse(it) }

    val initRange = -50..50

    steps
        .asSequence()
        .filter { it.xRange intersect initRange == it.xRange.toSet() }
        .filter { it.yRange intersect initRange == it.yRange.toSet() }
        .filter { it.zRange intersect initRange == it.zRange.toSet() }
        .fold(emptySet<Coord>()) { acc, step -> step.apply(acc) }
        .count()
        .let { println("Part1: $it") }
}
