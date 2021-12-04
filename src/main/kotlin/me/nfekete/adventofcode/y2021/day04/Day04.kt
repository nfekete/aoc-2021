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

private fun Iterator<List<String>>.toCards() = sequence {
    while (hasNext())
        yield(next().toCard())
}.toList()

private fun Card.findBingoRow(lastDrawn: Int, drawn: Set<Int>): Int? {
    val bingo = any { row -> row.all { it in drawn } }
    return if (bingo)
        lastDrawn * sumOf { row -> row.filter { number -> number !in drawn }.sum() }
    else
        null
}

private fun Card.findBingo(lastDrawn: Int, drawn: Set<Int>) =
    findBingoRow(lastDrawn, drawn)
        ?: transpose().findBingoRow(lastDrawn, drawn)

private data class BingoGame(val numbers: List<Int>, val cards: List<Card>)

private fun BingoGame.play() = sequence {
    var drawn = mutableSetOf<Int>()
    var cards = cards.toMutableList()
    numbers.forEach { number ->
        drawn.add(number)
        cards
            .mapNotNull { card -> card.findBingo(number, drawn)?.let { card to it } }
            .toList()
            .forEach { (card, score) ->
                cards.remove(card)
                yield(score)
            }
    }
}

private fun main() {
    val it = classpathFile("day04/input.txt").readLines().asSequence().chunkBy { it.isEmpty() }.iterator()
    val numbers = it.next().single().split(",").map { it.toInt() }
    val cards = it.toCards()

    val gamePlay = BingoGame(numbers, cards).play().toList()
    gamePlay.first().let { score -> println("Bingo! Part1 score = $score") }
    gamePlay.last().let { score -> println("Bingo! Part2 score = $score") }
}
