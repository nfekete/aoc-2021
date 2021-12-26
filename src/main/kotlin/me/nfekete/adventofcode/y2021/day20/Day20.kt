package me.nfekete.adventofcode.y2021.day20

import me.nfekete.adventofcode.y2021.common.chunkBy
import me.nfekete.adventofcode.y2021.common.classpathFile
import me.nfekete.adventofcode.y2021.common.map1
import me.nfekete.adventofcode.y2021.common.map2

fun parseTranslationTable(lines: List<String>) = lines.joinToString("")
    .map<Byte> { if (it == '#') 1 else 0 }.toByteArray()

data class Coord(val x: Int, val y: Int)

fun Coord.neighborhood() =
    (-1..1).flatMap { dy ->
        (-1..1).map { dx -> Coord(x + dx, y + dy) }
    }

val IntRange.bidiGrowByOne get() = first - 1..last + 1

data class Image(val bitmap: Array<ByteArray>, val backgroundOutside: Byte = 0) {
    val xRange = bitmap.first().indices
    val yRange = bitmap.indices
    operator fun get(coord: Coord) = with(coord) {
        if (x in xRange && y in yRange) {
            bitmap[y][x]
        } else backgroundOutside
    }

    fun translate(translationTable: ByteArray) =
        yRange.bidiGrowByOne.map { y ->
            xRange.bidiGrowByOne.map { x ->
                Coord(x, y)
                    .neighborhood()
                    .map { coord -> get(coord) }
                    .toBinary()
                    .let { translationTable[it] }
            }.toByteArray()
        }.toTypedArray().let { Image(it, translationTable.nextBackground(backgroundOutside)) }

    private fun ByteArray.nextBackground(backgroundOutside: Byte): Byte =
        if (backgroundOutside == 0.toByte())
            this.first()
        else
            this.last()

    private fun List<Byte>.toBinary() =
        fold(0) { acc, byte -> acc shl 1 or byte.toInt() }

    fun prettyPrint() =
        bitmap.joinToString("\n") { row -> row.joinToString("") { if (it == 1.toByte()) "#" else "." } }

    companion object
}

fun Image.Companion.parse(lines: List<String>) =
    lines.map { line ->
        line.map<Byte> { char -> if (char == '#') 1 else 0 }.toByteArray()
    }.toTypedArray().let { Image(it) }

fun main() {
    val (translationTable, image) = classpathFile("day20/input.txt")
        .lineSequence()
        .chunkBy { it.isEmpty() }
        .toList()
        .let { (a, b) -> Pair(a, b) }
        .map1 { lines -> parseTranslationTable(lines) }
        .map2 { lines -> Image.parse(lines) }

    image
        .translate(translationTable)
        .translate(translationTable)
        .bitmap.sumOf { row -> row.count { it == 1.toByte() } }
        .let { println("Part1: $it") }
}
