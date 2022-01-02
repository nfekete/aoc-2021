package me.nfekete.adventofcode.y2021.day25

import me.nfekete.adventofcode.y2021.common.classpathFile
import me.nfekete.adventofcode.y2021.common.takeWhileInclusive
import me.nfekete.adventofcode.y2021.day25.Direction.*
import me.nfekete.adventofcode.y2021.day25.GridElement.*

data class Coord(val x: Int, val y: Int)
enum class Direction { East, South }
enum class GridElement { EastFacingCucumber, SouthFacingCucumber, Empty }

fun GridElement.matches(direction: Direction) = when (direction) {
    East -> this == EastFacingCucumber
    South -> this == SouthFacingCucumber
}

data class CucumberMap(val grid: List<List<GridElement>>) {
    companion object {
        fun parse(lines: List<String>) =
            lines.map { line ->
                line.map { char ->
                    when (char) {
                        '>' -> EastFacingCucumber
                        'v' -> SouthFacingCucumber
                        '.' -> Empty
                        else -> error("Unknown grid element '$char'")
                    }
                }
            }.let { CucumberMap(it) }
    }

    val width: Int get() = grid.first().size
    val height: Int get() = grid.size
    val yRange: IntRange get() = grid.indices
    val xRange: IntRange get() = grid.first().indices

    fun Coord.forward(direction: Direction) = when (direction) {
        East -> copy(x = (x + 1) % width)
        South -> copy(y = (y + 1) % height)
    }

    fun Coord.backward(direction: Direction) = when (direction) {
        East -> copy(x = width + (x - 1) % width)
        South -> copy(y = height + (y - 1) % height)
    }

    operator fun get(coord: Coord) = grid[coord.y % height][coord.x % width]

    fun move(direction: Direction) =
        yRange.map { y ->
            xRange.map { x ->
                val coord = Coord(x, y)
                val current = this[coord]
                val previous = this[coord.backward(direction)]
                val next = this[coord.forward(direction)]
                when {
                    current.matches(direction) && next == Empty -> Empty
                    previous.matches(direction) && current == Empty -> previous
                    else -> current
                }
            }
        }.let { copy(grid = it) }

    fun advance() = move(East).move(South)
}

fun main() {
    val input = classpathFile("day25/input.txt")
        .readLines()
        .let { CucumberMap.parse(it) }

    generateSequence(input) { it.advance() }
        .windowed(2, 1, false)
        .takeWhileInclusive { (prev, current) -> prev != current }
        .count()
        .let { println("Part1: $it") }
}
