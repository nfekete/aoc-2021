package me.nfekete.adventofcode.y2021.day24

import me.nfekete.adventofcode.y2021.common.classpathFile
import java.util.*

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

class State(
    var input: String,
    val vars: MutableMap<Variable, Int> = EnumMap<Variable, Int>(Variable::class.java).apply {
        Variable.values().forEach { put(it, 0) }
    }
) {
    fun copy(input: String = this.input) = State(input = input, vars = EnumMap(vars))
}

object Interpreter {
    fun execute(initialState: State, program: List<Instruction>) =
        program.fold(initialState) { acc, instruction -> instruction.execute(acc) }

    fun Instruction.execute(state: State) =
        when (this) {
            is Inp -> state.apply { vars[target] = input.first().digitToInt(); input = input.drop(1) }
            is Add -> state.apply { vars[target] = vars[target]!! + argument.resolve(state) }
            is Mul -> state.apply { vars[target] = vars[target]!! * argument.resolve(state) }
            is Div -> state.apply { vars[target] = vars[target]!! / argument.resolve(state) }
            is Mod -> state.apply { vars[target] = vars[target]!! % argument.resolve(state) }
            is Eql -> state.apply { vars[target] = if (vars[target]!! == argument.resolve(state)) 1 else 0 }
        }

    fun Argument.resolve(state: State) =
        when (this) {
            is Number -> value
            Variable.w, Variable.x, Variable.y, Variable.z -> state.vars[this]!!
        }
}

fun List<Instruction>.part1(): String? {
    val blockSize = 18
    fun nextBlock(string: String, initialState: State, instructions: List<Instruction>): String? {
        if (string.length == 7) {
            println(string)
        }
        val currentBlock = instructions.take(blockSize)
        val remaining = instructions.drop(blockSize)
        for (char in '9' downTo '1') {
            val currentString = "$string$char"
            val state = Interpreter.execute(initialState.copy(input = "$char"), currentBlock)
            if (remaining.isEmpty()) {
                if (state.vars[Variable.z] == 0) {
                    return currentString
                }
            } else {
                nextBlock(currentString, state, remaining)
                    ?.let { return it }
            }
        }
        return null
    }
    return nextBlock("", State(""), this)
}

private fun main() {
    val instructions = classpathFile("day24/input.txt")
        .readLines()
        .map { Instruction.parse(it) }

    instructions.part1().let { println("Part1: $it") }

}
