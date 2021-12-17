package me.nfekete.adventofcode.y2021.day16

import me.nfekete.adventofcode.y2021.common.classpathFile
import me.nfekete.adventofcode.y2021.common.takeWhileInclusive
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

typealias Bit = Int

private sealed interface Packet {
    companion object

    val version: Int
    val size: Int

    data class LiteralValue(
        override val version: Int,
        val digits: List<Bit>,
    ) : Packet {
        override val size: Int
            get() = 6 + 5 * ((digits.size - 1) / 4 + 1)

        val bigInteger = digits.fold(BigInteger.ZERO) { acc, bit -> acc.shl(1) + bit.toBigInteger()}
    }

    data class Operator(
        override val version: Int,
        val packets: List<Packet>,
    ) : Packet {
        override val size: Int
            get() = 6 + 1 + packets.sumOf { it.size }
    }
}

private fun Iterator<Bit>.take(bits: Int) =
    asSequence().take(bits).toList().let {
        it.fold(0) { acc, next -> acc shl 1 or next }
    }

private fun <T> Iterator<T>.chunked(windowSize: Int) = let { other ->
    object : Iterator<List<T>> {
        private val buffer = ArrayList<T>(windowSize)

        override fun hasNext(): Boolean = buffer.isNotEmpty() || other.hasNext()

        override fun next(): List<T> {
            buffer.clear()
            while (other.hasNext() && buffer.size < windowSize) {
                buffer.add(other.next())
            }
            return buffer.toList()
        }
    }.asSequence()
}

private fun readPacket(bits: Iterator<Bit>): Packet {
    val version = bits.take(3)
    val typeId = bits.take(3)
    return when (typeId) {
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
            val lengthType = bits.take(1)
            when (lengthType) {
                0 -> {
                    val maxLength = bits.take(15)
                    var totalSize = 0
                    val subPackets = mutableListOf<Packet>()
                    while (totalSize < maxLength) {
                        val packet = readPacket(bits)
                        subPackets.add(packet)
                        totalSize += packet.size
                    }
                    Packet.Operator(version, subPackets.toList())
                }
                else -> {
                    val numberOfSubpackets = bits.take(11)
                    val subPackets = generateSequence { readPacket(bits) }
                        .take(numberOfSubpackets)
                        .toList()
                    Packet.Operator(version, subPackets)
                }
            }
        }
    }

}

private fun Packet.Companion.parse(string: String) =
    string
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

private fun Packet.sumVersions(): Int =
    when (this) {
        is Packet.LiteralValue -> version
        is Packet.Operator -> version + packets.sumOf { it.sumVersions() }
    }

private fun main() {
    val input = classpathFile("day16/input.txt").readLine()
    println(Packet.parse(input).sumVersions())
}

private class Day16Test {
    @ParameterizedTest
    @CsvSource(
        "D2FE28, 6",
        "38006F45291200, 9",
        "EE00D40C823060, 14",
        "8A004A801A8002F478, 16",
        "620080001611562C8802118E34, 12",
        "C0015000016115A2E0802F182340, 23",
        "A0016C880162017C3686B18A3D4780, 31",
    )
    fun providedExamplesPart1(hexCode: String, versionSum: Int) {
        val packet = Packet.parse(hexCode)
        val actual = packet.sumVersions()
        assertEquals(versionSum, actual)
    }

    @ParameterizedTest
    @CsvSource(
        "D2FE28, 2021, 21",
        "D14, 10, 11",
        "5224, 20, 16",
    )
    fun literalValueTests(hexCode: String, bigIntegerValue: BigInteger, bits: Int) {
        val literalValue = Packet.parse(hexCode) as Packet.LiteralValue
        assertEquals(bigIntegerValue, literalValue.bigInteger)
        assertEquals(bits, literalValue.size)
    }

    @Test
    fun operatorPacket_38006F45291200() {
        val operator = Packet.parse("38006F45291200") as Packet.Operator
        assertEquals(BigInteger.valueOf(10L), (operator.packets[0] as Packet.LiteralValue).bigInteger)
        assertEquals(BigInteger.valueOf(20L), (operator.packets[1] as Packet.LiteralValue).bigInteger)
    }

    @Test
    fun takeWhileTest() {
        run {
            val iterator = listOf(true, true, false).iterator()
            assertEquals(3, iterator.asSequence().takeWhileInclusive { it }.count())
            assertFalse(iterator.hasNext())
        }

        run {
            val iterator = listOf(true, true, false, true).iterator()
            assertEquals(3, iterator.asSequence().takeWhileInclusive { it }.count())
            assertTrue(iterator.hasNext())
            assertTrue(iterator.next())
            assertFalse(iterator.hasNext())
        }

        run {
            val iterator = listOf(1, 1, 2, 2, 3, 3, 4).iterator()
            assertEquals(3, iterator.chunked(2).takeWhileInclusive { it.first() < 3 }.count())
            assertTrue(iterator.hasNext())
            assertTrue(iterator.next() == 4)
            assertFalse(iterator.hasNext())
        }

        run {
            val iterator = listOf(false).iterator()
            assertEquals(1, iterator.asSequence().takeWhileInclusive { it }.count())
            assertFalse(iterator.hasNext())
        }
    }

}
