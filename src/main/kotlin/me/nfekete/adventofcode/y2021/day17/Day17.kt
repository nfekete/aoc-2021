package me.nfekete.adventofcode.y2021.day17

import me.nfekete.adventofcode.y2021.common.crossProduct

private data class TargetArea(val xRange: IntRange, val yRange: IntRange) {
    companion object
}

private val targetAreaRegex = Regex("target area: x=(-?\\d+)..(-?\\d+), y=(-?\\d+)..(-?\\d+)")
private fun TargetArea.Companion.parse(string: String) =
    targetAreaRegex.matchEntire(string)!!.destructured.toList().map { it.toInt() }.let { (x1, x2, y1, y2) ->
        TargetArea(x1..x2, y1..y2)
    }

private const val sample = "sample"
private const val input = "input"

private val Int.nextSpeedY get() = this - 1
private val Int.nextSpeedX
    get() = when {
        this < 0 -> this + 1
        this > 0 -> this - 1
        else -> 0
    }

private fun TargetArea.simulateY(v0: Int) =
    generateSequence(0 to v0) { (pos, speed) -> pos + speed to speed.nextSpeedY }
        .takeWhile { (pos, _) -> pos >= yRange.first }

private fun TargetArea.crossesTargetArea(v0x: Int, v0y: Int): Boolean {
    var x = 0
    var y = 0
    var vx = v0x
    var vy = v0y
    while (y >= yRange.first && x <= xRange.last) {
        if (x in xRange && y in yRange)
            return true
        x += vx
        y += vy
        vx = vx.nextSpeedX
        vy = vy.nextSpeedY
    }
    return false
}

private fun TargetArea.searchInitialVelocity(searchRange: IntRange, targetRange: IntRange) =
    searchRange
        .map { v0 -> v0 to simulateY(v0) }
        .filter { (_, seq) -> seq.any { (pos, _) -> pos in targetRange } }
        .map { (v0, seq) -> v0 to seq.maxOf { it.first } }

private fun TargetArea.searchInitialVelocity2D() =
    (0..xRange.last crossProduct yRange.first..-yRange.first)
        .filter { (v0x, v0y) -> crossesTargetArea(v0x, v0y) }

private fun TargetArea.solve() {
    searchInitialVelocity(0..-yRange.first, yRange)
        .maxOf { it.second }
        .let { println("Part1: $it") }

    searchInitialVelocity2D()
        .count()
        .let { println("Part2 X: $it") }
}

private fun main() {
    val inputs = mapOf(
        sample to "target area: x=20..30, y=-10..-5",
        input to "target area: x=156..202, y=-110..-69"
    ).mapValues { (_, value) -> TargetArea.parse(value) }

    inputs[input]!!.solve()
}
