package me.nfekete.adventofcode.y2021.day15

import me.nfekete.adventofcode.y2021.common.classpathFile
import java.util.PriorityQueue

private data class Coord(val x: Int, val y: Int)

private fun Coord.getNeighbors() = listOf(
    copy(x = x - 1),
    copy(x = x + 1),
    copy(y = y - 1),
    copy(y = y + 1),
)
private typealias Grid = List<List<Int>>

private val Grid.xRange get() = first().indices
private val Grid.yRange get() = indices
private fun Grid.isValidCoord(coord: Coord) = coord.x in xRange && coord.y in yRange
private operator fun Grid.get(coord: Coord) = this[coord.y][coord.x]

private fun Grid.shortestPath(source: Coord, target: Coord): Int {
    val costMap = mutableMapOf(source to 0)
    val prevMap = mutableMapOf<Coord, Coord>()

    val queue = PriorityQueue<Coord>(nullsLast(compareBy { costMap[it] })).apply { add(source) }
    while (queue.isNotEmpty()) {
        val current = queue.remove()
        val costToCurrent = costMap[current]!!
        current.getNeighbors()
            .filter(::isValidCoord)
            .forEach { neighbor ->
                val newCost = costToCurrent + get(neighbor)
                val oldCost = costMap[neighbor]
                if (oldCost == null || newCost < oldCost) {
                    costMap[neighbor] = newCost
                    prevMap[neighbor] = current
                    queue.offer(neighbor)
                }
            }
    }
    return costMap[target]!!
}

private fun main() {
    val input = classpathFile("day15/input.txt")
        .readLines()
        .map { line -> line.map { char -> char.digitToInt() } }

    input.shortestPath(Coord(0, 0), Coord(input.xRange.last, input.yRange.last))
        .let { println("Part1: $it") }
}
