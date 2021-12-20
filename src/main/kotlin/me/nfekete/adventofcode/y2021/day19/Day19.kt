package me.nfekete.adventofcode.y2021.day19

import me.nfekete.adventofcode.y2021.common.*

private data class Coord(val x: Int, val y: Int, val z: Int) : Comparable<Coord> {
    override fun compareTo(other: Coord): Int =
        when (this) {
            other -> 0
            else -> {
                val dist = x * x.toLong() + y * y + z * z
                val otherDist = other.run { x * x.toLong() + y * y + z * z }
                if (dist == otherDist) {
                    compareBy<Coord> { it.x }.thenBy { it.y }.thenBy { it.z }.compare(this, other)
                } else {
                    dist.compareTo(otherDist)
                }
            }
        }

    operator fun plus(other: Coord) = Coord(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Coord) = Coord(x - other.x, y - other.y, z - other.z)
    operator fun unaryMinus() = Coord(-x, -y, -z)
}
private typealias Matrix3x3 = List<List<Int>>

private val Matrix3x3.determinant
    get() =
        this[0][0] * this[1][1] * this[2][2] +
                this[0][1] * this[1][2] * this[2][0] +
                this[0][2] * this[1][0] * this[2][1] -
                this[0][2] * this[1][1] * this[2][0] -
                this[0][0] * this[1][2] * this[2][1] -
                this[0][1] * this[1][0] * this[2][2]

private operator fun Matrix3x3.times(coord: Coord) =
    with(coord) { listOf(x, y, z) }
        .let { vector ->
            (0..2).map { row ->
                (0..2).sumOf { column -> this[row][column] * vector[column] }
            }
        }
        .let { (x, y, z) -> Coord(x, y, z) }

private object Rotations {
    val all: List<Matrix3x3>

    init {
        val columns = (0..2).toSet()
        val values = listOf(-1, 1)
        fun row(index: Int, value: Int) = mutableListOf(0, 0, 0).apply { this[index] = value }.toList()
        all = columns.flatMap { c1 ->
            values.flatMap { v1 ->
                (columns - c1).flatMap { c2 ->
                    values.flatMap { v2 ->
                        values.map { v3 ->
                            val c3 = (columns - c1 - c2).single()
                            listOf(
                                row(c1, v1),
                                row(c2, v2),
                                row(c3, v3),
                            )
                        }
                    }
                }
            }
        }.filter { it.determinant == 1 }
    }
}

private interface Scanner {
    companion object

    val id: String
    val beacons: Set<Coord>
    fun rotate(rotation: Matrix3x3): Scanner = RotatedScanner(this, rotation)
    fun shift(delta: Coord): Scanner = ShiftedScanner(this, delta)
    fun allRotations() = Rotations.all.map { rotate(it) }
    fun merged(other: Scanner) = DefaultScanner("$id+${other.id}", (beacons union other.beacons).toSet())
}

private inline fun <reified T> equals(left: Any, right: Any?, equalsExtractor: (T) -> Any) =
    left === right || right is T && equalsExtractor(left as T) == equalsExtractor(right)

private class DefaultScanner(override val id: String, override val beacons: Set<Coord>) : Scanner {
    override fun toString() = id
    override fun equals(other: Any?) = equals(this, other, Scanner::id)
    override fun hashCode() = id.hashCode()
}

private class RotatedScanner(val delegate: Scanner, val rotation: Matrix3x3) : Scanner {
    override val id: String = delegate.id
    override val beacons: Set<Coord> get() = delegate.beacons.map { rotation * it }.toSet()
    override fun toString() = id
    override fun equals(other: Any?) = equals(this, other, Scanner::id)
    override fun hashCode() = id.hashCode()
}

private class ShiftedScanner(val delegate: Scanner, val delta: Coord) : Scanner {
    override val id: String = delegate.id
    override val beacons: Set<Coord> = delegate.beacons.map { it + delta }.toSet()

    override fun toString() = id
    override fun equals(other: Any?) = equals(this, other, Scanner::id)
    override fun hashCode() = id.hashCode()
}

private val scannerHeaderRegex = Regex("--- scanner (\\d+) ---")
private fun Scanner.Companion.parse(lines: List<String>): Scanner {
    val id = scannerHeaderRegex.matchEntire(lines.first())!!.destructured.component1()
    return lines.drop(1)
        .map { line -> line.split(",").map { it.toInt() }.let { (x, y, z) -> Coord(x, y, z) } }
        .let { DefaultScanner(id, it.toSet()) }
}

private fun Scanner.overlaps(other: Scanner): Scanner? = other.allRotations()
    .firstNotNullOfOrNull { rotatedOther ->
        beacons.flatMap { left ->
            rotatedOther.beacons.map { right ->
                left - right to rotatedOther
            }
        }.groupingBy { it }
            .eachCount()
            .map { it }
            .filter { it.value >= 12 }
            .maxByOrNull { it.value }
    }
    ?.let { match -> match.key.second.shift(match.key.first) }

private fun List<Scanner>.part1() {
    val ret = generateSequence(first() to drop(1)) { (merged, unmerged) ->
        findOverlappingScanner(merged, unmerged)
            ?.let { right -> merged.merged(right) to unmerged - right }
    }.last()
    println(ret)
    println(ret.first.beacons.size)
}

private fun findOverlappingScanner(left: Scanner, scanners: List<Scanner>, debug: Boolean = false): Scanner? =
    scanners.asSequence()
        .mapNotNull { right -> left.overlaps(right) }
        .firstOrNull()

private fun main() {
    val scanners = classpathFile("day19/input.txt")
        .lineSequence()
        .chunkBy { it.isEmpty() }
        .map { Scanner.parse(it) }
        .toList()

    scanners.part1()
}


