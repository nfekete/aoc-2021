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

private interface Grid {
    val xRange: IntRange
    val yRange: IntRange
    fun isValidCoord(coord: Coord) = coord.x in xRange && coord.y in yRange
    operator fun get(coord: Coord): Int

    private fun shortestPath(source: Coord, target: Coord): Int {
        val costMap = mutableMapOf(source to 0)
        val prevMap = mutableMapOf<Coord, Coord>()

        val queue = PriorityQueue<Coord>(nullsLast(compareBy { costMap[it] }))
            .apply { add(source) }
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

    fun shortestPathTopLeftToBottomRight() =
        shortestPath(Coord(xRange.first, yRange.first), Coord(xRange.last, yRange.last))
}

private class SimpleGrid(val enteringCosts: List<List<Int>>) : Grid {
    override val xRange: IntRange
        get() = enteringCosts.first().indices
    override val yRange: IntRange
        get() = enteringCosts.indices

    override fun get(coord: Coord) = enteringCosts[coord.y][coord.x]
}

private class ComplexGrid(val enteringCosts: List<List<Int>>) : Grid {
    override val xRange: IntRange
        get() = 0 until enteringCosts.first().size * 5
    override val yRange: IntRange
        get() = 0 until enteringCosts.size * 5

    override fun get(coord: Coord): Int {
        val divx = coord.x / enteringCosts.first().size
        val divy = coord.y / enteringCosts.size
        val modx = coord.x % enteringCosts.first().size
        val mody = coord.y % enteringCosts.size
        return (enteringCosts[mody][modx] + divx + divy - 1) % 9 + 1
    }
}

private fun main() {
    val input = classpathFile("day15/input.txt")
        .readLines()
        .map { line -> line.map { char -> char.digitToInt() } }

    SimpleGrid(input).shortestPathTopLeftToBottomRight()
        .let { println("Part1: $it") }
    ComplexGrid(input).shortestPathTopLeftToBottomRight()
        .let { println("Part2: $it") }
}
