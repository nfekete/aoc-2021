package me.nfekete.adventofcode.y2021.day24

import me.nfekete.adventofcode.y2021.common.classpathFile

sealed interface Argument
enum class Variable : Argument { w, x, y, z }
data class Number(val value: Int) : Argument

sealed interface Instruction {
    companion object
}

data class Inp(val target: Variable) : Instruction
data class Add(val target: Variable, val argument: Argument) : Instruction
data class Mul(val target: Variable, val argument: Argument) : Instruction
data class Div(val target: Variable, val argument: Argument) : Instruction
data class Mod(val target: Variable, val argument: Argument) : Instruction
data class Eql(val target: Variable, val argument: Argument) : Instruction

val instructionRegex = Regex("([a-z]{3}) ([wxyz]) ?(?:(-?\\d+)|([wxyz]))?")
fun Instruction.Companion.parse(string: String) =
    instructionRegex.matchEntire(string)!!
        .destructured
        .let { (instr, target, number, argRef) ->
            val argument = number.takeIf { it.isNotEmpty() }?.let { Number(it.toInt()) }
                ?: argRef.takeIf { it.isNotEmpty() }?.let { Variable.valueOf(it) }
            val target = Variable.valueOf(target)
            when (instr) {
                "inp" -> Inp(target)
                "add" -> Add(target, argument!!)
                "mul" -> Mul(target, argument!!)
                "div" -> Div(target, argument!!)
                "mod" -> Mod(target, argument!!)
                "eql" -> Eql(target, argument!!)
                else -> error("Unknown instruction '$instr'")
            }
        }

class State(val input: Iterator<Char>) {
    val variables = Variable.values().associateWith { 0 }.toMutableMap()
}

class Part1 {
    fun execute(input: String, program: List<Instruction>) = program.fold(State(input.iterator())) { acc, instruction ->
        instruction.execute(acc)
    }

    fun Instruction.execute(state: State) =
        when (this) {
            is Inp -> state.apply { variables[target] = input.next().digitToInt() }
            is Add -> state.apply { variables[target] = variables[target]!! + argument.resolve(state) }
            is Mul -> state.apply { variables[target] = variables[target]!! * argument.resolve(state) }
            is Div -> state.apply { variables[target] = variables[target]!! / argument.resolve(state) }
            is Mod -> state.apply { variables[target] = variables[target]!! % argument.resolve(state) }
            is Eql -> state.apply { variables[target] = if (variables[target]!! == argument.resolve(state)) 1 else 0 }
        }

    fun Argument.resolve(state: State) =
        when (this) {
            is Number -> value
            Variable.w, Variable.x, Variable.y, Variable.z -> state.variables[this]!!
        }
}

private fun main() {
    val instructions = classpathFile("day24/input.txt")
        .readLines()
        .map { Instruction.parse(it) }

    (99_999_999_999_999 downTo 10_000_000_000)
        .onEach { if (it % 1_000_000_000 == 0L) println("progress: $it") }
        .map { it.toString() }
        .find { Part1().execute(it, instructions).variables[Variable.z] == 1 }
        .let { println(it) }

}
