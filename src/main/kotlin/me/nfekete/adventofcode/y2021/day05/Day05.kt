package me.nfekete.adventofcode.y2021.day05

import me.nfekete.adventofcode.y2021.common.classpathFile

private data class Point(val x: Int, val y: Int)
private data class Line(val start: Point, val end: Point) {
    companion object
    fun isHorizontal() = start.y == end.y
    fun isVertical() = start.x == end.x
}

private val lineRegex = Regex("(\\d+),(\\d+) -> (\\d+),(\\d+)")
private fun Line.Companion.parse(line: String) =
    lineRegex
        .matchEntire(line)!!
        .destructured
        .let { (x1, y1, x2, y2) ->
            Line(Point(x1.toInt(), y1.toInt()), Point(x2.toInt(), y2.toInt()))
        }

private class Raster(private val width: Int, private val height: Int) {
    private val points = IntArray(width * height)

    fun inc(x: Int, y: Int) = points[y * width + x]++
    fun count(predicate: (Int) -> Boolean) = points.count(predicate)
}

private fun inOrder(a: Int, b: Int) = if (a < b) a to b else b to a
private fun List<Line>.maxX() = flatMap { listOf(it.start.x, it.end.x) }.maxOrNull()!!
private fun List<Line>.maxY() = flatMap { listOf(it.start.y, it.end.y) }.maxOrNull()!!

private fun main() {
    val input = classpathFile("day05/input.txt")
        .readLines()
        .map { Line.parse(it) }

    val raster = Raster(input.maxX() + 1, input.maxY() + 1)
    input.filter { it.isVertical() }
        .flatMap { verticalLine ->
            val (startY, endY) = inOrder(verticalLine.start.y, verticalLine.end.y)
            (startY..endY).map { verticalLine.start.x to it }
        }
        .plus(
            input.filter { it.isHorizontal() }
                .flatMap { horizontalLine ->
                    val (startX, endX) = inOrder(horizontalLine.start.x, horizontalLine.end.x)
                    (startX..endX).map { it to horizontalLine.start.y }
                }
        )
        .forEach { (x, y) -> raster.inc(x, y) }

    raster.count { it >= 2 }.let { println("Part1: $it") }
}
