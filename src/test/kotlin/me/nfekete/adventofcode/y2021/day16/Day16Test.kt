package me.nfekete.adventofcode.y2021.day16

import me.nfekete.adventofcode.y2021.common.chunked
import me.nfekete.adventofcode.y2021.common.takeWhileInclusive
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
        "C200B40A82, 3",
        "04005AC33890, 54",
        "880086C3E88112, 7",
        "CE00C43D881120, 9",
        "D8005AC2A8F0, 1",
        "F600BC2D8F, 0",
        "9C005AC2F8F0, 0",
        "9C0141080250320F1802104A08, 1",
    )
    fun valueTests(hexCode: String, value: Value) {
        val packet = Packet.parse(hexCode)
        println(packet)
        assertEquals(value, packet.value)
    }

    @ParameterizedTest
    @CsvSource(
        "D2FE28, 2021, 21",
        "D14, 10, 11",
        "5224, 20, 16",
    )
    fun literalValueTests(hexCode: String, value: Value, bits: Int) {
        val literalValue = Packet.parse(hexCode) as Packet.LiteralValue
        assertEquals(value, literalValue.value)
        assertEquals(bits, literalValue.size)
    }

    @Test
    fun `operatorPacket 38006F45291200`() {
        val operator = Packet.parse("38006F45291200") as Packet.Operator
        assertEquals(10, (operator.packets[0] as Packet.LiteralValue).value)
        assertEquals(20, (operator.packets[1] as Packet.LiteralValue).value)
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
