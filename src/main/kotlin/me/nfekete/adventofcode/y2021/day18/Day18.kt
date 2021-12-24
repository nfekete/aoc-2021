package me.nfekete.adventofcode.y2021.day18

import me.nfekete.adventofcode.y2021.common.classpathFile
import me.nfekete.adventofcode.y2021.common.produceIf

sealed interface Number {
    operator fun plus(other: Number): Number = Paired(this, other).run {
        generateSequence(this as Number) { number ->
            CombinedReducer().run { reduce(number).takeIf { reduced } }
        }.last()
    }

    val magnitude: Int

    companion object
}

data class Paired(val first: Number, val second: Number) : Number {
    override fun toString() = "[$first,$second]"
    override val magnitude: Int
        get() = 3 * first.magnitude + 2 * second.magnitude
}

data class Value(val value: Int) : Number {
    override fun toString() = "$value"
    override val magnitude: Int
        get() = value
}

class NumberParser(string: String) {

    private val characterQueue = string.toCollection(ArrayDeque())

    fun expect(expected: Char) {
        val removed = characterQueue.removeFirst()
        if (removed != expected) {
            error("Expected '$expected' but got '$removed instead")
        }
    }

    fun parse(): Number = expectPair()
    private fun expectPair(): Paired {
        expect('[')
        val first = expectNumber()
        expect(',')
        val second = expectNumber()
        expect(']')
        return Paired(first, second)
    }

    private fun expectNumber() = when (val head = characterQueue.first()) {
        in '0'..'9' -> expectValue()
        '[' -> expectPair()
        else -> error("Unexpected character '$head'")
    }

    private fun expectValue(): Value {
        var value = 0
        while (characterQueue.first() in '0'..'9') {
            value = value * 10 + characterQueue.removeFirst().digitToInt()
        }
        return Value(value = value)
    }
}

fun Number.Companion.parse(string: String) = NumberParser(string).parse()

interface Reducer {
    val reduced: Boolean
    fun reduce(number: Number): Number
}

class ExplosionReducer : Reducer {
    var lastValue: Value? = null
    var left: Number? = null
    var right: Number? = null
    var exploded = false

    override val reduced get() = exploded
    override fun reduce(number: Number) =
        number.reduceExploding()!!
            .let { number ->
                lastValue?.let { lastValue ->
                    left?.let { left ->
                        number.add(lastValue, left as Value)
                            .also { this.left = null }
                    }
                } ?: number
            }

    private fun Number.reduceExploding(nesting: Int = 0): Number? =
        when (this) {
            is Paired -> {
                if (!exploded && nesting == 4) {
                    left = first
                    right = second
                    exploded = true
                    null
                } else {
                    val first = first.reduceExploding(nesting + 1) ?: Value(0)
                    val second = second.reduceExploding(nesting + 1) ?: Value(0)
                    Paired(first, second)
                }
            }
            is Value -> {
                if (exploded) {
                    if (right != null) {
                        Value(this.value + (right as Value).value)
                            .also { right = null }
                    } else {
                        this
                    }
                } else {
                    lastValue = this
                    this
                }
            }
        }

    private fun Number.add(value: Value, add: Value): Number =
        when (this) {
            is Paired -> Paired(first.add(value, add), second.add(value, add))
            is Value -> {
                if (this === value) {
                    Value(this.value + add.value)
                } else {
                    this
                }
            }
        }
}

class SplitReducer : Reducer {
    var split = false
    override val reduced get() = split
    override fun reduce(number: Number): Number =
        when (number) {
            is Paired ->
                produceIf(!split) { Paired(reduce(number.first), reduce(number.second)) } ?: number
            is Value ->
                if (!split && number.value >= 10)
                    Paired(Value(number.value / 2), Value((number.value + 1) / 2))
                        .also { split = true }
                else
                    number
        }
}

class CombinedReducer : Reducer {
    override var reduced = false
    override fun reduce(number: Number): Number {
        val first = ExplosionReducer()
        val result = first.reduce(number)
        reduced = reduced || first.reduced
        if (reduced) {
            return result
        } else {
            val second = SplitReducer()
            val result = second.reduce(number)
            reduced = reduced || second.reduced
            return result
        }
    }
}

private fun main() {
    val input = classpathFile("day18/input.txt")
        .readLines()
        .map { Number.parse(it) }

    input.reduce { acc, number -> acc + number }.let { println("Part1: ${it.magnitude}") }
}
