package me.nfekete.adventofcode.y2021.day24

import me.nfekete.adventofcode.y2021.common.classpathFile

private sealed interface Argument
private enum class Variable : Argument { w, x, y, z }
private data class Number(val value: Long) : Argument

private sealed interface Instruction {
    companion object
}

private data class Inp(val target: Variable) : Instruction
private data class Add(val target: Variable, val argument: Argument) : Instruction
private data class Mul(val target: Variable, val argument: Argument) : Instruction
private data class Div(val target: Variable, val argument: Argument) : Instruction
private data class Mod(val target: Variable, val argument: Argument) : Instruction
private data class Eql(val target: Variable, val argument: Argument) : Instruction

private val instructionRegex = Regex("([a-z]{3}) ([wxyz]) ?(?:(-?\\d+)|([wxyz]))?")
private fun Instruction.Companion.parse(string: String) =
    instructionRegex.matchEntire(string)!!
        .destructured
        .let { (instr, targetName, number, argRef) ->
            val argument = number.takeIf { it.isNotEmpty() }?.let { Number(it.toLong()) }
                ?: argRef.takeIf { it.isNotEmpty() }?.let { Variable.valueOf(it) }
            val target = Variable.valueOf(targetName)
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

/*
private data class State(
    var input: String,
    val vars: MutableMap<Variable, Long> = Variable.values().associateWithTo(EnumMap(Variable::class.java)) { 0 }
) {
    fun copy(input: String = this.input) = State(input = input, vars = EnumMap(vars))
}

private object Interpreter {
    fun execute(initialState: State, program: List<Instruction>) =
        program.fold(initialState) { acc, instruction -> instruction.execute(acc) }

    fun Instruction.execute(state: State) =
        when (this) {
            is Inp -> state.apply { vars[target] = input.first().digitToInt().toLong(); input = input.drop(1) }
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
 */

private data class FunctionParameters(
    val dz: Long,
    val d1: Long,
    val d2: Long,
)

private fun List<Instruction>.extractParams() =
    chunked(18).map { chunk ->
        val dz = ((chunk[4] as Div).argument as Number).value
        val d1 = ((chunk[5] as Add).argument as Number).value
        val d2 = ((chunk[15] as Add).argument as Number).value
        FunctionParameters(dz, d1, d2)
    }

private fun calcZ(w: Int, z: Long, dz: Long, d1: Long, d2: Long) =
    if (z % 26L + d1 != w.toLong())
        26 * (z / dz) + w + d2
    else
        (z/dz)

private fun List<Instruction>.findValidModelNumber(ascending: Boolean = false): String? {
    val stepParameters = extractParams()
    val digitRange = if (ascending) 1..9 else 9 downTo 1

    fun recurse(step: Int, z: Long): String? {
        for (digit in digitRange) {
            val parameters = stepParameters[step]
            if (parameters.dz == 26L && (z % 26 + parameters.d1) != digit.toLong())
                continue
            val newZ = calcZ(digit, z, parameters.dz, parameters.d1, parameters.d2)
            if (step == 13) {
                if (newZ == 0L) {
                    return digit.digitToChar().toString()
                } else {
                    continue
                }
            } else {
                val result = recurse(step + 1, newZ)
                if (result != null) {
                    return "" + digit.digitToChar() + result
                }
            }
        }
        return null
    }

    return recurse(0, 0)
}

private fun main() {
    val instructions = classpathFile("day24/input.txt")
        .readLines()
        .map { Instruction.parse(it) }

    instructions.findValidModelNumber(false).let { println("Part1: $it") }
    instructions.findValidModelNumber(true).let { println("Part2: $it") }
}
