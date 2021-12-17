package me.nfekete.adventofcode.y2021.day16

import me.nfekete.adventofcode.y2021.common.chunked
import me.nfekete.adventofcode.y2021.common.classpathFile
import me.nfekete.adventofcode.y2021.common.takeWhileInclusive
import me.nfekete.adventofcode.y2021.day16.Packet.OperatorLengthType.*
import me.nfekete.adventofcode.y2021.day16.Packet.OperatorType.*

typealias Bit = Int
typealias Value = Long

sealed interface Packet {
    companion object

    val version: Int
    val size: Int
    val value: Value

    data class LiteralValue(
        override val version: Int,
        val digits: List<Bit>,
    ) : Packet {
        override val size: Int
            get() = 6 + 5 * ((digits.size - 1) / 4 + 1)
        override val value = digits.fold(0L) { acc, bit -> acc.shl(1) + bit }
    }

    data class Operator(
        override val version: Int,
        val operatorLengthType: OperatorLengthType,
        val operatorType: OperatorType,
        val packets: List<Packet>,
    ) : Packet {
        override val size get() = 6 + 1 + operatorLengthType.requiredBits + packets.sumOf { it.size }
        override val value get() = operatorType.apply(packets)
    }

    enum class OperatorLengthType(val requiredBits: Int) {
        BITS(15), PACKETS(11)
    }

    enum class OperatorType {
        Sum,
        Product,
        Minimum,
        Maximum,
        GreaterThan,
        LessThan,
        EqualTo,
        ;

        companion object {
            fun of(typeId: Int) = when (typeId) {
                0 -> Sum
                1 -> Product
                2 -> Minimum
                3 -> Maximum
                5 -> GreaterThan
                6 -> LessThan
                7 -> EqualTo
                else -> error("Unknown operator type '$typeId'")
            }
        }
    }

}

private fun Packet.OperatorType.apply(operands: List<Packet>) = when (this) {
    Sum -> operands.sumOf { it.value }
    Product -> operands.fold(1L) { acc, current -> acc * current.value }
    Minimum -> operands.minOf { it.value }
    Maximum -> operands.maxOf { it.value }
    GreaterThan -> if (operands[0].value > operands[1].value) 1 else 0
    LessThan -> if (operands[0].value < operands[1].value) 1 else 0
    EqualTo -> if (operands[0].value == operands[1].value) 1 else 0
}

private fun Iterator<Bit>.take(bits: Int) =
    asSequence().take(bits).toList().let {
        it.fold(0) { acc, next -> acc shl 1 or next }
    }

private fun readPacket(bits: Iterator<Bit>): Packet {
    val version = bits.take(3)
    return when (val typeId = bits.take(3)) {
        4 -> {
            val numberBits = bits
                .chunked(5)
                .takeWhileInclusive { it.first() == 1 }
                .map { it.drop(1) }
                .flatten()
                .toList()
            Packet.LiteralValue(version, numberBits)
        }
        else -> {
            when (bits.take(1)) {
                0 -> {
                    val maxLength = bits.take(15)
                    var totalSize = 0
                    val subPackets = mutableListOf<Packet>()
                    while (totalSize < maxLength) {
                        val packet = readPacket(bits)
                        subPackets.add(packet)
                        totalSize += packet.size
                    }
                    Packet.Operator(version, BITS, Packet.OperatorType.of(typeId), subPackets.toList())
                }
                else -> {
                    val numberOfSubpackets = bits.take(11)
                    val subPackets = generateSequence { readPacket(bits) }
                        .take(numberOfSubpackets)
                        .toList()
                    Packet.Operator(version, PACKETS, Packet.OperatorType.of(typeId), subPackets)
                }
            }
        }
    }

}

fun Packet.Companion.parse(string: String) =
    string
        .asSequence()
        .map { it.digitToInt(16) }
        .flatMap {
            listOf(
                it and 8 shr 3,
                it and 4 shr 2,
                it and 2 shr 1,
                it and 1,
            )
        }
        .let { sequence ->
            val bits = sequence.iterator()
            val packet = readPacket(bits)
            packet
        }

fun Packet.sumVersions(): Int =
    when (this) {
        is Packet.LiteralValue -> version
        is Packet.Operator -> version + packets.sumOf { it.sumVersions() }
    }

private fun main() {
    val input = classpathFile("day16/input.txt").readLine()
    val packet = Packet.parse(input)
    println("Part1: ${packet.sumVersions()}")
    println("Part2: ${packet.value}")
}
