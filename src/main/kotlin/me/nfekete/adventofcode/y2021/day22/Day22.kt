package me.nfekete.adventofcode.y2021.day22

import me.nfekete.adventofcode.y2021.common.classpathFile
import me.nfekete.adventofcode.y2021.common.intersect
import me.nfekete.adventofcode.y2021.day22.Signal.OFF
import me.nfekete.adventofcode.y2021.day22.Signal.ON

val IntRange.length get() = if (isEmpty()) 0 else last - first + 1

data class IntRange3D(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    fun isEmpty() = xRange.isEmpty() || yRange.isEmpty() || zRange.isEmpty()
    fun volume() = xRange.length.toLong() * yRange.length * zRange.length
    infix fun intersect(other: IntRange3D) =
        IntRange3D(
            xRange intersect other.xRange,
            yRange intersect other.yRange,
            zRange intersect other.zRange
        )
}

enum class Signal { ON, OFF }
data class Step(val signal: Signal, val range3D: IntRange3D) {
    companion object

    fun apply(steps: List<Step>) =
        when (signal) {
            ON ->
                steps
                    .plus(this)
                    .plus(
                        steps.filter { it.signal == ON }
                            .map { it.range3D intersect this.range3D }
                            .filter { !it.isEmpty() }
                            .map { Step(OFF, it) }
                    ).plus(
                        steps.filter { it.signal == OFF }
                            .map { it.range3D intersect this.range3D }
                            .filter { !it.isEmpty() }
                            .map { Step(ON, it) }
                    )
            OFF ->
                steps
                    .plus(
                        steps
                            .filter { it.signal == ON }
                            .map { it.range3D intersect this.range3D }
                            .filter { !it.isEmpty() }
                            .map { Step(OFF, it) }
                    ).plus(
                        steps
                            .filter { it.signal == OFF }
                            .map { it.range3D intersect this.range3D }
                            .filter { !it.isEmpty() }
                            .map { Step(ON, it) }
                    )
        }
}

fun Iterable<Step>.volume() = sumOf { if (it.signal == ON) it.range3D.volume() else -it.range3D.volume() }

val stepRegex = Regex("(on|off) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")
fun Step.Companion.parse(string: String) =
    stepRegex.matchEntire(string)!!.destructured.let { (command, x1, x2, y1, y2, z1, z2) ->
        Step(
            Signal.valueOf(command.uppercase()),
            IntRange3D(x1.toInt()..x2.toInt(), y1.toInt()..y2.toInt(), z1.toInt()..z2.toInt())
        )
    }

fun main() {
    val steps = classpathFile("day22/input.txt")
        .readLines()
        .map { Step.parse(it) }

    val initRange = (-50..50).let { IntRange3D(it, it, it) }

    steps
        .filter { it.range3D intersect initRange == it.range3D }
        .fold(emptyList<Step>()) { acc, step -> step.apply(acc) }
        .volume()
        .let { println("Part1: $it") }

    steps
        .fold(emptyList<Step>()) { acc, step -> step.apply(acc) }
        .volume()
        .let { println("Part1: $it") }
}
