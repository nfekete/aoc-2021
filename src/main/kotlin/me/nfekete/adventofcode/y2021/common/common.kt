package me.nfekete.adventofcode.y2021.common

import java.io.BufferedReader
import java.io.InputStreamReader

fun classpathFile(path: String) =
    BufferedReader(
        InputStreamReader(
            Thread.currentThread().contextClassLoader.getResourceAsStream(path)!!
        )
    )

fun String.translate(vararg chars: Pair<Char, Char>) = chars.toMap().let { map ->
    this.map { map.getOrDefault(it, it) }.joinToString("")
}

fun Sequence<String>.chunkBy(predicate: (String) -> Boolean) = sequence {
    val currentChunk = mutableListOf<String>()
    for (line in this@chunkBy) {
        if (predicate(line)) {
            yield(currentChunk.toList())
            currentChunk.clear()
        } else {
            currentChunk.add(line)
        }
    }
    yield(currentChunk.toList())
}

fun String.splitByDelimiter(delimiter: Char) =
    indexOf(delimiter)
        .let { index -> take(index) to substring(index + 1) }

fun String.splitByDelimiter(delimiter: String) =
    indexOf(delimiter)
        .let { index -> take(index) to substring(index + delimiter.length) }

fun <A, B, C> Pair<A, B>.map1(fn: (A) -> C): Pair<C, B> = let { (a, b) -> fn(a) to b }
fun <A, B, C> Pair<A, B>.map2(fn: (B) -> C): Pair<A, C> = let { (a, b) -> a to fn(b) }
fun <A, B, R> Pair<A, B>.map(fn: (A, B) -> R): R = let { (a, b) -> fn(a,b) }
val <A, B> Pair<A, B>.swapped get() = second to first

fun Iterable<Long>.product() = fold(1L) { acc, i -> acc * i }
fun Sequence<Long>.product() = fold(1L) { acc, i -> acc * i }

fun <R> crossProduct(ra: IntRange, rb: IntRange, fn: (Int, Int) -> R) =
    ra.flatMap { a -> rb.map { b -> fn(a, b) } }

fun <R> crossProduct(ra: IntRange, rb: IntRange, rc: IntRange, fn: (Int, Int, Int) -> R) =
    ra.flatMap { a -> rb.flatMap { b -> rc.map { c -> fn(a, b, c) } } }

fun <R> crossProduct(ra: IntRange, rb: IntRange, rc: IntRange, rd: IntRange, fn: (Int, Int, Int, Int) -> R) =
    ra.flatMap { a -> rb.flatMap { b -> rc.flatMap { c -> rd.map { d -> fn(a, b, c, d) } } } }

fun <A, B, R> crossProduct(sa: Sequence<A>, sb: Sequence<B>, fn: (A, B) -> R) =
    sa.flatMap { a -> sb.map { b -> fn(a, b) } }

fun <A, B, C, R> crossProduct(sa: Sequence<A>, sb: Sequence<B>, sc: Sequence<C>, fn: (A, B, C) -> R) =
    sa.flatMap { a -> sb.flatMap { b -> sc.map { c -> fn(a, b, c) } } }

fun <A, B, C, D, R> crossProduct(
    sa: Sequence<A>, sb: Sequence<B>, sc: Sequence<C>, sd: Sequence<D>, fn: (A, B, C, D) -> R
) = sa.flatMap { a -> sb.flatMap { b -> sc.flatMap { c -> sd.map { d -> fn(a, b, c, d) } } } }

fun <T> List<List<T>>.transpose() = first().indices.map { columnIndex -> map { row -> row[columnIndex] } }

fun <P, R> ((P) -> R).memoized(cache: MutableMap<P, R> = mutableMapOf()): (P) -> R =
    fun(p: P) =
        if (p in cache) {
            cache[p]!!
        } else {
            val r = this(p)
            cache[p] = r
            r
        }

fun <P1, P2, R> ((P1, P2) -> R).memoized(cache: MutableMap<Pair<P1, P2>, R> = mutableMapOf()): (P1, P2) -> R =
    fun(p1: P1, p2: P2) = (p1 to p2).let {
        if (it in cache) {
            cache[it]!!
        } else {
            val r = this(it.first, it.second)
            cache[it] = r
            r
        }
    }
