package me.nfekete.adventofcode.y2021.day14

import me.nfekete.adventofcode.y2021.common.*

private data class InsertionRule(val pair: String, val insertion: String) {
    companion object
}

private fun InsertionRule.Companion.parse(string: String) = string.splitByDelimiter(" -> ").map(::InsertionRule)

private class RuleSet(rules: Iterable<InsertionRule>) {
    val ruleMap = rules.associate { it.pair to it.insertion }

    fun generateCountingSequence(template: String) =
        template.windowed(2, 1, false)
            .map { pair -> TokenCounter(pair, 1) }
            .let { list ->
                generateSequence(Tokens(list)) { quantityTracker -> quantityTracker.apply(this) }
            }
}

private data class TokenCounter(val token: String, val count: Long)
private data class Tokens(
    val first: TokenCounter,
    val mid: List<TokenCounter>,
    val last: TokenCounter
)

private fun Tokens(counters: List<TokenCounter>) =
    Tokens(counters.first(), counters.drop(1).dropLast(1), counters.last())

private fun List<TokenCounter>.normalize() =
    groupBy { it.token }
        .mapValues { (_, list) -> list.sumOf { it.count } }
        .entries
        .map { (pair, count) -> TokenCounter(pair, count) }

private fun Tokens.apply(ruleSet: RuleSet) =
    listOf(first)
        .plus(mid)
        .plus(last)
        .flatMap {
            when (val insertion = ruleSet.ruleMap[it.token]) {
                null -> listOf(it)
                else -> listOf(
                    it.mapToken { token -> token[0] + insertion },
                    it.mapToken { token -> insertion + token[1] },
                )
            }
        }
        .let { counters ->
            Tokens(
                counters.first(),
                counters.drop(1)
                    .dropLast(1)
                    .normalize(),
                counters.last()
            )
        }

private fun TokenCounter.mapToken(fn: (String) -> String) = TokenCounter(fn(token), count)
private fun Tokens.result() =
    first.token.map { char -> TokenCounter(char.toString(), first.count) }
        .plus(mid.map { tokenCounter -> tokenCounter.mapToken { token -> token.drop(1) } })
        .plus(last.mapToken { token -> token.drop(1) })
        .normalize()
        .let { list -> list.maxOf { it.count } - list.minOf { it.count } }

private fun main() {
    val input = classpathFile("day14/input.txt").readLines()
    val template = input.first()
    val insertionRules = input.drop(2).map { InsertionRule.parse(it) }.let(::RuleSet)

    insertionRules.generateCountingSequence(template)
        .drop(10)
        .first()
        .result()
        .let { println("Part1: $it") }

    insertionRules.generateCountingSequence(template)
        .drop(40)
        .first()
        .result()
        .let { println("Part2: $it") }
}
