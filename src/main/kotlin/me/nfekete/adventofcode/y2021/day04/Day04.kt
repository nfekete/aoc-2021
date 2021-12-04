package me.nfekete.adventofcode.y2021.day04

import me.nfekete.adventofcode.y2021.common.chunkBy
import me.nfekete.adventofcode.y2021.common.classpathFile
import me.nfekete.adventofcode.y2021.common.transpose

typealias Card = List<List<Int>>

private fun List<String>.toCard() = map { line ->
    line.split(" ")
        .filter { it.isNotEmpty() }
        .map { it.toInt() }
}

private fun Card.findBingoRow(lastDrawn: Int, drawn: Set<Int>): Int? {
    val hasBingo = any { row -> row.all { it in drawn } }
    return if (hasBingo)
        lastDrawn * sumOf { it.filter { number -> number !in drawn }.sum() }
    else
        null
}

private fun Card.findBingo(lastDrawn: Int, drawn: Set<Int>) =
    findBingoRow(lastDrawn, drawn)
        ?: transpose().findBingoRow(lastDrawn, drawn)

private fun Iterator<List<String>>.toCards() = sequence {
    while (hasNext())
        yield(next().toCard())
}.toList()

private data class BingoGame(val numbers: List<Int>, val cards: List<Card>)

private fun BingoGame.play(): Int {
    var set = mutableSetOf<Int>()
    numbers.forEach { number ->
        set.add(number)
        val bingo = cards.firstNotNullOfOrNull { card ->
            card.findBingo(number, set)
        }
        if (bingo != null) {
            return bingo
        }
    }
    error("invalid input")
}

private fun main() {
    val it = classpathFile("day04/input.txt").readLines().asSequence().chunkBy { it.isEmpty() }.iterator()
    val numbers = it.next().single().split(",").map { it.toInt() }
    val cards = it.toCards()

    BingoGame(numbers, cards).play().let { score -> println("Bingo! Score = $score") }
}
