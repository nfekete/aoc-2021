package me.nfekete.adventofcode.y2021.day05

import me.nfekete.adventofcode.y2021.common.classpathFile
import kotlin.math.abs

private data class Point(val x: Int, val y: Int)
private data class Line(val start: Point, val end: Point) {
    companion object

    fun isHorizontal() = start.y == end.y
    fun isVertical() = start.x == end.x
    fun isDiagonal() = abs(start.x - end.x) == abs(start.y - end.y)
    val xRange get() = if (start.x <= end.x) start.x..end.x else start.x downTo end.x
    val yRange get() = if (start.y <= end.y) start.y..end.y else start.y downTo end.y
}

private val lineRegex = Regex("(\\d+),(\\d+) -> (\\d+),(\\d+)")
private fun Line.Companion.parse(line: String) =
    lineRegex
        .matchEntire(line)!!
        .destructured
        .let { (x1, y1, x2, y2) ->
            Line(Point(x1.toInt(), y1.toInt()), Point(x2.toInt(), y2.toInt()))
        }

private fun List<Line>.pointsOfHorizontalLines() =
    filter { it.isHorizontal() }
        .flatMap { horizontalLine ->
            horizontalLine.xRange.map { Point(it, horizontalLine.start.y) }
        }

private fun List<Line>.pointsOfVerticalLines() =
    filter { it.isVertical() }
        .flatMap { verticalLine ->
            verticalLine.yRange.map { Point(verticalLine.start.x, it) }
        }

private fun List<Line>.pointsOfDiagonalLines() =
    filter { it.isDiagonal() }
        .flatMap { diagonalLine ->
            diagonalLine.xRange.zip(diagonalLine.yRange, ::Point)
        }

private fun List<Point>.countOverlaps() =
    groupingBy { it }
        .eachCount()
        .values.count { it >= 2 }

private fun main() {
    val input = classpathFile("day05/input.txt")
        .readLines()
        .map { Line.parse(it) }

    val part1Lines = input.pointsOfVerticalLines()
        .plus(input.pointsOfHorizontalLines())

    part1Lines
        .countOverlaps()
        .let { println("Part1: $it") }

    part1Lines.plus(input.pointsOfDiagonalLines())
        .countOverlaps()
        .let { println("Part2: $it") }
}
