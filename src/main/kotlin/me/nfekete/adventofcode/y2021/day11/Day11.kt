package me.nfekete.adventofcode.y2021.day11

import me.nfekete.adventofcode.y2021.common.classpathFile

private typealias Grid = List<List<Int>>

data class Coord(val rowIndex: Int, val columnIndex: Int)

private val Coord.neighbors
    get() = (-1..1).flatMap { deltaRow ->
        (-1..1).map { deltaColumn ->
            copy(rowIndex = rowIndex + deltaRow, columnIndex = columnIndex + deltaColumn)
        }
    } - this
private val Grid.rowIndices get() = indices
private val Grid.columnIndices get() = first().indices
private val Grid.coords
    get() = rowIndices.flatMap { rowIndex ->
        columnIndices.map { columnIndex -> Coord(rowIndex, columnIndex) }
    }
private operator fun Grid.get(coord: Coord) = this[coord.rowIndex][coord.columnIndex]
private fun Grid.isValidCoord(coord: Coord) = coord.rowIndex in rowIndices && coord.columnIndex in columnIndices
private fun Grid.flashingCoords() = coords.filter { coord -> this[coord] > 9 }.toSet()
private fun Grid.mapIndices(transform: Grid.(Coord) -> Int) = indices.map { rowIndex ->
    this[rowIndex].indices.map { columnIndex ->
        this.transform(Coord(rowIndex, columnIndex))
    }
}
fun Grid.pretty() = joinToString("\n") { it.joinToString("")}
private fun Grid.increment(neighbors: List<Coord>) =
    mapIndices { coord ->
        this[coord] + if (coord in neighbors) 1 else 0
    }
private fun Grid.zeroOut(coords: Set<Coord>) =
    mapIndices { coord ->
        if (coord in coords) 0 else this[coord]
    }
private fun Grid.elements() = flatMap { row -> row.map { it } }
private fun Grid.allTheSame() = elements().run { !all { it == first() } }

fun Grid.part1() = generateSequence(this to emptySet<Coord>()) { (startingGrid, _) ->
    var grid = startingGrid.map { row -> row.map { element -> element + 1 } }
    val initialFlash = grid.flashingCoords()
    val queue = ArrayDeque(initialFlash)
    val flashed = mutableSetOf<Coord>().apply { addAll(initialFlash) }
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        flashed.add(current)
        val neighbors = current.neighbors.filter { grid.isValidCoord(it) }
        grid = grid.increment(neighbors)
        val flashingCoords = grid.flashingCoords()
        queue.addAll(flashingCoords - flashed)
        grid = grid.zeroOut(flashingCoords)
    }
    grid.zeroOut(flashed) to flashed
}

private fun main() {
    val input = classpathFile("day11/input.txt")
        .readLines()
        .map { line -> line.map { char -> char.digitToInt() } }

    input.part1()
        .take(101)
        .sumOf { it.second.size }
        .let { println("Part1: $it") }

    input.part1()
        .takeWhile { (grid, _) -> grid.allTheSame() }
        .count()
        .let { println("Part2: $it") }

}
