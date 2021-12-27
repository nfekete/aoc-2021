package me.nfekete.adventofcode.y2021.day21

import me.nfekete.adventofcode.y2021.common.takeWhileInclusive

private data class PlayerPositions(val player1: Int, val player2: Int) {
    companion object {
        private val parseRegex = Regex("Player ([12]) starting position: (\\d+)")
        fun parse(lines: List<String>) = lines.mapIndexed { index, line ->
            val (playerNumber, startPosition) = parseRegex.matchEntire(line)!!.destructured
            check(playerNumber.toInt() == index + 1) { "Unexpected player number" }
            startPosition.toInt()
        }.let { (p1, p2) -> PlayerPositions(p1, p2) }
    }
}

private class Game(val startingPositions: PlayerPositions) {
    data class PlayerState(val position: Int, val score: Int) {
        fun newPosition(rollValue: Int) = (position + rollValue - 1) % 10 + 1
    }

    data class State(val player1: PlayerState, val player2: PlayerState) {
        constructor(startingPositions: PlayerPositions) :
                this(
                    PlayerState(startingPositions.player1, 0),
                    PlayerState(startingPositions.player2, 0)
                )
    }

    private fun dice() = generateSequence(0, Int::inc).map { 1 + it % 100 }
    private fun rolls() = dice().chunked(3).map { it.sum() }
    private fun PlayerState.calculateNewState(rollValue: Int) =
        newPosition(rollValue).let { newPos ->
            copy(
                position = newPos,
                score = score + newPos
            )
        }

    val winningScore = 1000
    fun play() =
        rolls().runningFoldIndexed(State(startingPositions)) { index, acc, rollValue ->
            when (index % 2) {
                0 -> acc.copy(player1 = acc.player1.calculateNewState(rollValue))
                else -> acc.copy(player2 = acc.player2.calculateNewState(rollValue))
            }
        }.takeWhileInclusive { it.player1.score < winningScore && it.player2.score < winningScore }
            .mapIndexed { index, playerPositions -> index * 3 to playerPositions }
}

@Suppress("UNUSED_VARIABLE")
private fun main() {
    val input = """
        Player 1 starting position: 8
        Player 2 starting position: 4
    """.trimIndent()
    val sample = """
        Player 1 starting position: 4
        Player 2 starting position: 8
    """.trimIndent()

    val startingPositions = PlayerPositions.parse(input.lines())
    val game = Game(startingPositions)
    game.play().last().let { (rounds, state) ->
        val score = rounds * listOf(state.player1.score, state.player2.score).single { it < game.winningScore }
        println("Part1: $rounds x $state = $score")
    }
}
