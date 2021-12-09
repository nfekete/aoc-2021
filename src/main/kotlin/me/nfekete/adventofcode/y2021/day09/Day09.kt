package me.nfekete.adventofcode.y2021.day09

import me.nfekete.adventofcode.y2021.common.classpathFile
import me.nfekete.adventofcode.y2021.common.product

private fun <T> List<List<T>>.transpose() =
    first().indices.map { column ->
        map { row -> row[column] }
    }

private fun List<Int>.indicesOfLocalMinima() =
    indices.filter { i ->
        when (i) {
            0 -> get(i) < get(i + 1)
            size - 1 -> i == size-1 && get(i) < get(i-1)
            else -> get(i-1) > get(i) && get(i) < get(i+1)
        }
    }

typealias Input = List<List<Int>>
data class Coord(val row: Int, val column: Int)
fun Input.withinBounds(coord: Coord) = coord.row in indices && coord.column in first().indices
operator fun Input.get(coord: Coord) = this[coord.row][coord.column]
val Coord.up get() = copy(row = row - 1)
val Coord.down get() = copy(row = row + 1)
val Coord.left get() = copy(column = column - 1)
val Coord.right get() = copy(column = column + 1)

private fun Input.findBasin(lowPoint: Coord): Set<Coord> {
    val visited = mutableSetOf<Coord>()
    val queue = ArrayDeque<Coord>().apply { add(lowPoint) }
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        visited.add(current)
        val neighbors = listOf(current.up, current.right, current.down, current.left)
            .filter { withinBounds(it) }
            .minus(visited)
        queue.addAll(neighbors.filter { get(it) != 9 })
    }
    return visited
}

private fun main() {
    val input = classpathFile("day09/input.txt")
        .readLines()
        .map { line -> line.asIterable().map { char -> char.digitToInt() } }

    val horizontalLocalMinimae = input.flatMapIndexed { rowIndex, row ->
        row.indicesOfLocalMinima().map { columnIndex -> Coord(rowIndex, columnIndex) }
    }
    val verticalLocalMinimae = input.transpose().flatMapIndexed { columnIndex, column ->
        column.indicesOfLocalMinima().map { rowIndex -> Coord(rowIndex, columnIndex) }
    }
    val localMinimae = horizontalLocalMinimae intersect verticalLocalMinimae.toSet()

    localMinimae.sumOf { input[it] + 1 }
        .let { println("Part1: $it") }

    localMinimae.map { lowPoint -> input.findBasin(lowPoint) }
        .map { it.size.toLong() }
        .sortedDescending()
        .take(3)
        .product()
        .let { println("Part2: $it") }

}
