package me.nfekete.adventofcode.y2021.day10

import me.nfekete.adventofcode.y2021.common.classpathFile

private val opening = "([{<".toSet()
private val closing = ")]}>".toSet()
private val matchingOpening = (closing zip opening).associate { it }

private fun String.runSyntaxChecker() =
    asSequence()
        .runningFold("" to 0) { (stack, score), char ->
            when (char) {
                in opening -> stack + char to score
                else -> {
                    val pop = stack.last()
                    val newStack = stack.dropLast(1)
                    if (pop == matchingOpening[char])
                        newStack to score
                    else when (char) {
                        ')' -> newStack to score + 3
                        ']' -> newStack to score + 57
                        '}' -> newStack to score + 1197
                        '>' -> newStack to score + 25137
                        else -> error("Unhandled char '$char'")
                    }
                }
            }
        }

private fun String.syntaxErrorScore(): Int =
    runSyntaxChecker()
        .map { it.second }
        .firstOrNull { it != 0 } ?: 0

private fun completionScore(string: String) =
    string.reversed().fold(0L) { acc, c -> acc * 5 + opening.indexOf(c) + 1 }

private fun String.completionScoreWhenIncomplete(): Long? =
    runSyntaxChecker()
        .toList()
        .last()
        .takeIf { it.second == 0 }
        ?.let { completionScore(it.first) }

private fun <T> List<T>.middleElement() = get(size / 2)

private fun main() {
    val input = classpathFile("day10/input.txt")
        .readLines()

    input.sumOf { it.syntaxErrorScore() }
        .let { println("Part1: $it") }

    input.mapNotNull { it.completionScoreWhenIncomplete() }
        .sorted()
        .toList()
        .middleElement()
        .let { println("Part2: $it") }
}
