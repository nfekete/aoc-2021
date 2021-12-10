package me.nfekete.adventofcode.y2021.day10

import me.nfekete.adventofcode.y2021.common.classpathFile

private val opening = "([{<".toSet()
private val closing = ")]}>".toSet()
private val matchingOpening = (closing zip opening).associate { it }

private fun String.part1(): Int = ArrayDeque<Char>().let { queue ->
    forEach { char ->
        when (char) {
            in opening -> queue.addLast(char)
            else -> {
                val pop = queue.removeLastOrNull()
                if (pop != matchingOpening[char]) {
                    when (char) {
                        ')' -> return@let 3
                        ']' -> return@let 57
                        '}' -> return@let 1197
                        '>' -> return@let 25137
                    }
                }
            }
        }
    }
    return 0
}

private fun main() {
    val input = classpathFile("day10/input.txt")
        .readLines()

    input.sumOf { it.part1() }.let { println(it) }
}
