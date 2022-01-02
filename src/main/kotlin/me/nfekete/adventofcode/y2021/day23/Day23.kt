package me.nfekete.adventofcode.y2021.day23

import me.nfekete.adventofcode.y2021.common.classpathFile
import me.nfekete.adventofcode.y2021.common.produceIf
import me.nfekete.adventofcode.y2021.common.range
import java.util.PriorityQueue

private const val hallwayY = 1
private val hallwayRangeX = 1..11
private val hallwayStopCoordinatesX = hallwayRangeX - AmphipodType.values().map { it.roomCoordinateX }.toSet()

private enum class AmphipodType(val energyUse: Int) {
    A(1), B(10), C(100), D(1000)
}

private val AmphipodType.roomCoordinateX get() = 3 + ordinal * 2

private data class Coord(val x: Int, val y: Int)
private data class Amphipod(val type: AmphipodType, val location: Coord)
private data class BurrowState(val amphipods: List<Amphipod>, val roomsRangeY: IntRange) {
    companion object {
        fun parse(lines: List<String>) =
            lines.flatMapIndexed { y, line ->
                line.mapIndexedNotNull { x, char ->
                    char.takeIf { it in 'A'..'D' }?.let { Amphipod(enumValueOf("$it"), Coord(x, y)) }
                }
            }.let { amphipods ->
                BurrowState(amphipods, amphipods.minOf { it.location.y }..amphipods.maxOf { it.location.y })
            }
    }

    fun Coord.isTargetRoomFor(amphipodType: AmphipodType) = x == amphipodType.roomCoordinateX
    fun Coord.get() = amphipods.singleOrNull { it.location.x == x && it.location.y == y }
    val Amphipod.inDestinationRoom get() = location.isTargetRoomFor(type)
    fun Amphipod.isOnHallway() = location.y == hallwayY
    fun Amphipod.isInFinalPosition() = inDestinationRoom &&
            (location.y + 1..roomsRangeY.last).all { y ->
                val atLocation = amphipods.singleOrNull { it.location.x == location.x && it.location.y == y }
                atLocation != null && atLocation.inDestinationRoom
            }

    fun Amphipod.isValidFinalDestination(y: Int) =
        Coord(type.roomCoordinateX, y).get() == null &&
                (y + 1..roomsRangeY.last).all { y ->
                    val atLocation = Coord(type.roomCoordinateX, y).get()
                    atLocation != null && atLocation.inDestinationRoom
                }

    fun Amphipod.pathToDestinationRoom(y: Int) = (location.x to type.roomCoordinateX).range.drop(1).map { x -> Coord(x, hallwayY) }
        .plus((hallwayY to y).range.drop(1).map { y -> Coord(type.roomCoordinateX, y) })

    fun Amphipod.pathToHallway(x: Int) = (location.y to hallwayY).range.drop(1).map { y -> Coord(location.x, y) }
        .plus((location.x to x).range.drop(1).map { x -> Coord(x, hallwayY) })

    fun List<Coord>.isClear() = all { coord -> amphipods.none { it.location == coord } }
    fun teleport(amphipod: Amphipod, target: Coord) =
        copy(amphipods = this.amphipods.map { produceIf(it === amphipod) { it.copy(location = target) } ?: it })

    fun allInFinalPosition() = amphipods.all { it.isInFinalPosition() }
}

private class AmphipodOrganizer {
    data class BurrowStateWithCost(val state: BurrowState, val cost: Int)

    fun BurrowState.expand() =
        amphipods
            .flatMap { amphipod ->
                if (amphipod.isInFinalPosition())
                    emptyList()
                else if (amphipod.isOnHallway()) {
                    roomsRangeY
                        .filter { y -> amphipod.isValidFinalDestination(y) }
                        .map { y -> amphipod.pathToDestinationRoom(y) }
                        .filter { path -> path.isClear() }
                        .map { path ->
                            BurrowStateWithCost(teleport(amphipod, path.last()), path.size * amphipod.type.energyUse)
                        }
                } else {
                    val map = hallwayStopCoordinatesX
                        .map { x -> amphipod.pathToHallway(x) }
                    val filter = map
                        .filter { path -> path.isClear() }
                    val map1 = filter
                        .map { path ->
                            BurrowStateWithCost(teleport(amphipod, path.last()), path.size * amphipod.type.energyUse)
                        }
                    map1
                }
            }

    fun organize(burrowState: BurrowState): Int {
        val costMap = mutableMapOf(burrowState to 0)
        val queue = PriorityQueue(compareBy(BurrowStateWithCost::cost)).apply { add(BurrowStateWithCost(burrowState, 0)) }
        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val costToCurrent = costMap[current.state]!!
            current.state.expand()
                .forEach {
                    val newCost = costToCurrent + it.cost
                    val oldCost = costMap[it.state]
                    if (oldCost == null || newCost < oldCost) {
                        costMap[it.state] = newCost
                        queue.offer(it)
                    }
                }
        }
        return costMap.entries.filter { (state, _) -> state.allInFinalPosition() }.minOf { it.value }
    }
}

private fun main() {
    listOf(
        "sample",
        "input",
    ).forEach { name ->
        val part1 = classpathFile("day23/$name.txt").readLines()
        val additionalLines = """
            |  #D#C#B#A#
            |  #D#B#A#C#
        """.trimMargin("|")
        val part2 = part1.take(3) + additionalLines.lines() + part1.drop(3)

        AmphipodOrganizer().organize(BurrowState.parse(part1)).let { println("$name: Part1: $it") }
        AmphipodOrganizer().organize(BurrowState.parse(part2)).let { println("$name: Part2: $it") }
    }
}
