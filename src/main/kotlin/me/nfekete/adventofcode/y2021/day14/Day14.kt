package me.nfekete.adventofcode.y2021.day14

import me.nfekete.adventofcode.y2021.common.*

private data class InsertionRule(val pair: String, val insertion: Char) {
    companion object
}

private fun InsertionRule.Companion.parse(string: String) =
    string.splitByDelimiter(" -> ").map { pair, insertion -> InsertionRule(pair, insertion.first()) }

private class RuleSet(rules: Iterable<InsertionRule>) {
    val ruleMap = rules.associate { it.pair to "${it.pair.first()}${it.insertion}${it.pair.last()}" }
    private fun apply(template: String) =
        template.windowed(2, 1, false)
            .map { ruleMap[it] ?: it }
            .let { list ->
                list.take(1) + list.drop(1).map { it.drop(1) }
            }.joinToString("")

    fun generateInsertionSequence(template: String) = generateSequence(template, ::apply)
}

private fun main() {
    val input = classpathFile("day14/input.txt").readLines()
    val template = input.first()
    val insertionRules = input.drop(2).map { InsertionRule.parse(it) }.let(::RuleSet)

    insertionRules.generateInsertionSequence(template)
        .drop(10)
        .first()
        .toList()
        .groupingBy { it }
        .eachCount()
        .let { map -> map.entries.maxOf { it.value } - map.entries.minOf { it.value } }
        .let { println("Part1: $it") }
}
