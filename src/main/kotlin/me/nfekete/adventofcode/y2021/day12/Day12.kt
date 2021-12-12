package me.nfekete.adventofcode.y2021.day12

import me.nfekete.adventofcode.y2021.common.classpathFile
import me.nfekete.adventofcode.y2021.common.swapped

private typealias Node = String
private typealias Edge = Pair<String, String>

private class Graph(input: Set<Edge>) {
    val edges = input + input.map { it.swapped }
    val nodeMap = edges
        .groupBy { it.first }
        .mapValues { (_, value) ->
            value.map { it.second }.toSet()
        }
}

private fun String.isSmall() = all { it.isLowerCase() }

private fun Graph.findAllPaths(
    start: Node = "start",
    end: Node = "end",
    unvisitableNodes: (Map<Node, Int>) -> Set<Node> = { it.keys }
) = sequence {
    suspend fun SequenceScope<List<Node>>.dfs(node: Node, path: List<Node>, visited: Map<Node, Int>) {
        if (node == end) {
            yield(path)
            return
        }
        val newVisited = if (node.isSmall())
            visited.plus(node to visited.getOrDefault(node, 0) + 1)
        else
            visited
        val neighbors = nodeMap[node].orEmpty()
        val visitableNeighbors = neighbors - unvisitableNodes(newVisited) - start
        visitableNeighbors.forEach { neighbor ->
            dfs(neighbor, path + neighbor, newVisited)
        }
    }
    dfs(start, listOf(start), emptyMap())
}


private fun main() {
    val graph = classpathFile("day12/input.txt")
        .readLines()
        .map { it.split("-") }
        .map { (from, to) -> Edge(from, to) }
        .toSet()
        .let(::Graph)

    graph
        .findAllPaths()
        .count()
        .let { println("Part1: $it") }

    graph
        .findAllPaths { visited ->
            if (visited.values.any { it == 2 })
                visited.keys
            else emptySet()
        }
        .count()
        .let { println("Part2: $it") }
}
