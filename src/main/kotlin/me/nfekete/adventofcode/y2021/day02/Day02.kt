package me.nfekete.adventofcode.y2021.day02

import me.nfekete.adventofcode.y2021.common.classpathFile

private sealed class Instruction {
    companion object
    class Forward(val value: Int) : Instruction()
    class Down(val value: Int) : Instruction()
    class Up(val value: Int) : Instruction()
}

private val regex = Regex("([^ ]+) (\\d+)")
private fun Instruction.Companion.parse(instruction: String) =
    regex.matchEntire(instruction)!!.destructured.let { (command, param) ->
        when (command) {
            "forward" -> Instruction.Forward(param.toInt())
            "down" -> Instruction.Down(param.toInt())
            "up" -> Instruction.Up(param.toInt())
            else -> error("unknown command '$command'")
        }
    }

private interface Interpreter<S> {
    val initialState: S
    fun interpret(state: S, instruction: Instruction): S
    fun run(instructions: Iterable<Instruction>) =
        instructions.fold(initialState, this::interpret)
}

private class Part1 : Interpreter<Part1.Position> {
    data class Position(val horizontal: Int = 0, val depth: Int = 0)
    override val initialState: Position get() = Position()
    override fun interpret(state: Position, instruction: Instruction) =
        when (instruction) {
            is Instruction.Forward -> state.copy(horizontal = state.horizontal + instruction.value)
            is Instruction.Down -> state.copy(depth = state.depth + instruction.value)
            is Instruction.Up -> state.copy(depth = state.depth - instruction.value)
        }
}

private class Part2 : Interpreter<Part2.Position> {
    data class Position(val horizontal: Int = 0, val depth: Int = 0, val aim: Int = 0)
    override val initialState: Position get() = Position()
    override fun interpret(state: Position, instruction: Instruction): Position {
        return when (instruction) {
            is Instruction.Down -> state.copy(aim = state.aim + instruction.value)
            is Instruction.Up -> state.copy(aim = state.aim - instruction.value)
            is Instruction.Forward -> state.copy(
                horizontal = state.horizontal + instruction.value,
                depth = state.depth + instruction.value * state.aim
            )
        }
    }
}

private fun main() {
    val instructions = classpathFile("day02/input.txt")
        .readLines()
        .map { Instruction.parse(it) }

    Part1().run(instructions).let { println("Part1 result: $it -> ${it.depth * it.horizontal}") }
    Part2().run(instructions).let { println("Part2 result: $it -> ${it.depth * it.horizontal}") }
}
