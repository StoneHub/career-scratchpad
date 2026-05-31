package com.monroestone.careerscratchpad.labs.secretmessage

fun secretMessageSingleMethodCode(): String {
    return """
fun printSecretMessage(input: String): String {
    // The pasted table may include column labels. Remove those before grouping triples.
    val headerTokens = setOf(
        "x-coordinate",
        "x",
        "character",
        "char",
        "y-coordinate",
        "y"
    )

    val dataTokens = input
        .lines()
        .flatMap { it.trim().split(Regex("\\s+")) }
        .filter { it.isNotBlank() }
        .filterNot { it.lowercase() in headerTokens }

    // Store each plotted character by coordinate: (x, y) -> character.
    val pixelsByCoordinate = mutableMapOf<Pair<Int, Int>, String>()

    for (triple in dataTokens.chunked(3)) {
        if (triple.size != 3) continue

        val x = triple[0].toIntOrNull() ?: continue
        val character = triple[1].take(1)
        val y = triple[2].toIntOrNull() ?: continue

        // The document uses ░ for background pixels. A space is easier to read.
        pixelsByCoordinate[x to y] = if (character == "░") " " else character
    }

    if (pixelsByCoordinate.isEmpty()) {
        println("")
        return ""
    }

    val maxX = pixelsByCoordinate.keys.maxOf { it.first }
    val maxY = pixelsByCoordinate.keys.maxOf { it.second }

    // Coordinates use y = 0 as the bottom row, so print from the highest y down.
    val output = buildString {
        for (y in maxY downTo 0) {
            for (x in 0..maxX) {
                append(pixelsByCoordinate[x to y] ?: " ")
            }
            if (y != 0) append('\n')
        }
    }

    // The exercise asks us to print the decoded grid. Returning it helps with tests.
    println(output)
    return output
}
    """.trimIndent()
}
