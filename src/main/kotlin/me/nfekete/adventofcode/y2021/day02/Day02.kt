package me.nfekete.adventofcode.y2021.day02

import me.nfekete.adventofcode.y2021.common.classpathFile

sealed class Instruction {
    companion object
    class Forward(val distance: Int) : Instruction()
    class Down(val distance: Int) : Instruction()
    class Up(val distance: Int) : Instruction()
}

val regex = Regex("([^ ]+) (\\d+)")
fun Instruction.Companion.parse(instruction: String) =
    regex.matchEntire(instruction)!!.destructured.let { (command, param) ->
        when (command) {
            "forward" -> Instruction.Forward(param.toInt())
            "down" -> Instruction.Down(param.toInt())
            "up" -> Instruction.Up(param.toInt())
            else -> error("unknown command '$command'")
        }
    }

interface Interpreter<S> {
    fun interpret(state: S, instruction: Instruction): S
}

data class SubmarinePosition(val horizontal: Int = 0, val depth: Int = 0)
class Part1 : Interpreter<SubmarinePosition> {
    override fun interpret(state: SubmarinePosition, instruction: Instruction) =
        when (instruction) {
            is Instruction.Forward -> state.copy(horizontal = state.horizontal + instruction.distance)
            is Instruction.Down -> state.copy(depth = state.depth + instruction.distance)
            is Instruction.Up -> state.copy(depth = state.depth - instruction.distance)
        }
}

fun main() {
    val part1 = Part1()
    classpathFile("day02/input.txt")
        .readLines()
        .map { Instruction.parse(it) }
        .fold(SubmarinePosition(), part1::interpret)
        .let { println("Part1 result: $it -> ${it.depth * it.horizontal}") }
}
