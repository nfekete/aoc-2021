package me.nfekete.adventofcode.y2021.day13

import me.nfekete.adventofcode.y2021.common.chunkBy
import me.nfekete.adventofcode.y2021.common.classpathFile

private data class Coord(val x: Int, val y: Int)
private sealed class Fold {
    data class X(val x: Int) : Fold()
    data class Y(val y: Int) : Fold()
}

private val foldRegex = Regex("^fold along ([xy])=(\\d+)$")
private fun Fold(s: String) = foldRegex.matchEntire(s)!!.destructured.let { (axis, value) ->
    when (axis) {
        "x" -> Fold.X(value.toInt())
        "y" -> Fold.Y(value.toInt())
        else -> error("Unknown axis '$axis'")
    }
}

private fun List<Coord>.foldAt(fold: Fold) = map { coord ->
    when (fold) {
        is Fold.X -> when {
            coord.x < fold.x -> coord
            else -> coord.copy(x = 2 * fold.x - coord.x)
        }
        is Fold.Y -> when {
            coord.y < fold.y -> coord
            else -> coord.copy(y = 2 * fold.y - coord.y)
        }
    }
}

private fun main() {
    val (coords, folds) = classpathFile("day13/input.txt").useLines { lines ->
        val (coordLines, foldLines) = lines.chunkBy { it.isEmpty() }
            .toList()
        val coords = coordLines
            .map { line -> line.split(",").map { it.toInt() } }
            .map { (x, y) -> Coord(x, y) }
        val folds = foldLines.map { Fold(it) }
        coords to folds
    }

    coords.foldAt(folds.first())
        .toSet()
        .count()
        .let { println("Part1: $it") }
}
