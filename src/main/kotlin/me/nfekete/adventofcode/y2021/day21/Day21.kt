package me.nfekete.adventofcode.y2021.day21

import me.nfekete.adventofcode.y2021.common.crossProduct
import me.nfekete.adventofcode.y2021.common.takeWhileInclusive
import kotlin.math.max

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

private class DiracGame(val startingPositions: PlayerPositions) {
    val winningScore = 21
    val diceSumDistribution = crossProduct(1..3, 1..3, 1..3) { a, b, c -> a + b + c }
        .groupingBy { it }.eachCount()

    fun newPosition(position: Int, rollValue: Int) = (position + rollValue - 1) % 10 + 1
    private data class PlayerState(val position: Int, val rolls: Int, val score: Int, val frequency: Long)

    private fun List<PlayerState>.computeFrequencies() =
        filter { s -> s.score < winningScore }
            .flatMap { s ->
                diceSumDistribution.map { (rollValue, rollFreq) ->
                    val newPosition = newPosition(s.position, rollValue)
                    PlayerState(
                        position = newPosition,
                        rolls = s.rolls + 1,
                        score = s.score + newPosition,
                        frequency = s.frequency * rollFreq
                    )
                }
            }
            .groupingBy { listOf(it.position, it.rolls, it.score) }
            .fold(0L) { accumulator, element -> accumulator + element.frequency }
            .map { (k, v) ->
                PlayerState(
                    position = k[0],
                    rolls = k[1],
                    score = k[2],
                    v
                )
            }

    private fun playerStates(startingPosition: Int) =
        generateSequence(
            listOf(PlayerState(startingPosition, 0, 0, 1L))
        ) { it.computeFrequencies() }
            .takeWhileInclusive { it.any { diracState -> diracState.score < winningScore } }
            .flatten()

    fun play(): Pair<Long, Long> {
        val player1 = playerStates(startingPositions.player1)
        val player2 = playerStates(startingPositions.player2)

        val player1Wins = player1.crossProduct(player2)
            .filter { (p1, p2) -> p1.rolls == p2.rolls + 1 }
            .filter { (p1, _) -> p1.score >= winningScore }
            .filter { (_, p2) -> p2.score < winningScore }
            .sumOf { (p1, p2) -> p1.frequency * p2.frequency }

        val player2Wins = player1.crossProduct(player2)
            .filter { (p1, p2) -> p1.rolls == p2.rolls }
            .filter { (p1, _) -> p1.score < winningScore }
            .filter { (_, p2) -> p2.score >= winningScore }
            .sumOf { (p1, p2) -> p1.frequency * p2.frequency }

        return player1Wins to player2Wins
    }
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

    val diracGame = DiracGame(startingPositions)
    diracGame.play().also { (p1wins, p2wins) ->
        val moreWins = max(p1wins, p2wins)
        println("player1 wins: $p1wins")
        println("player2 wins: $p2wins")
        println("Part2: $moreWins")
    }
}
