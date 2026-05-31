package com.monroestone.careerscratchpad.labs.secretmessage

data class SecretMessageExampleTable(
    val label: String,
    val tableText: String
)

fun secretMessageExampleTables(): List<SecretMessageExampleTable> {
    return listOf(
        SecretMessageExampleTable("Load HI", plainTextToCoordinateTable("HI")),
        SecretMessageExampleTable("Load F", plainTextToCoordinateTable("F")),
        SecretMessageExampleTable("Load HCMIDBO", plainTextToCoordinateTable("HCMIDBO"))
    )
}

fun plainTextToCoordinateTable(text: String): String {
    val rows = mutableListOf("x\tcharacter\ty")
    var cursorX = 0

    text.uppercase().forEach { character ->
        if (character == ' ') {
            cursorX += 3
            return@forEach
        }

        val glyph = blockGlyphs[character] ?: blankGlyph
        glyph.forEachIndexed { topRowIndex, row ->
            val y = GlyphHeight - 1 - topRowIndex
            row.forEachIndexed { xOffset, pixel ->
                val x = cursorX + xOffset
                val value = if (pixel == '#') "█" else "░"
                rows += "$x\t$value\t$y"
            }
        }
        cursorX += GlyphWidth + 1
    }

    return rows.joinToString("\n")
}

fun formatCoordinateTableText(text: String): String {
    val tokens = text.split(Regex("\\s+"))
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .dropHeaderTokens()

    if (tokens.isEmpty() || tokens.size % 3 != 0) return text

    val rows = tokens.chunked(3).map { triple ->
        val x = triple[0].toIntOrNull() ?: return text
        val y = triple[2].toIntOrNull() ?: return text
        "$x\t${triple[1].take(1)}\t$y"
    }

    return listOf("x\tcharacter\ty")
        .plus(rows)
        .joinToString("\n")
}

private fun List<String>.dropHeaderTokens(): List<String> {
    val firstThree = take(3).map { it.lowercase() }
    return if (
        firstThree.size == 3 &&
        firstThree[0] in setOf("x", "x-coordinate") &&
        firstThree[1] in setOf("char", "character", "value") &&
        firstThree[2] in setOf("y", "y-coordinate")
    ) {
        drop(3)
    } else {
        this
    }
}

private const val GlyphWidth = 5
private const val GlyphHeight = 7

private val blankGlyph = listOf(
    ".....",
    ".....",
    ".....",
    ".....",
    ".....",
    ".....",
    "....."
)

private val blockGlyphs = mapOf(
    'A' to listOf(
        ".###.",
        "#...#",
        "#...#",
        "#####",
        "#...#",
        "#...#",
        "#...#"
    ),
    'B' to listOf(
        "####.",
        "#...#",
        "#...#",
        "####.",
        "#...#",
        "#...#",
        "####."
    ),
    'C' to listOf(
        ".####",
        "#....",
        "#....",
        "#....",
        "#....",
        "#....",
        ".####"
    ),
    'D' to listOf(
        "####.",
        "#...#",
        "#...#",
        "#...#",
        "#...#",
        "#...#",
        "####."
    ),
    'E' to listOf(
        "#####",
        "#....",
        "#....",
        "####.",
        "#....",
        "#....",
        "#####"
    ),
    'F' to listOf(
        "#####",
        "#....",
        "#....",
        "####.",
        "#....",
        "#....",
        "#...."
    ),
    'G' to listOf(
        ".####",
        "#....",
        "#....",
        "#.###",
        "#...#",
        "#...#",
        ".####"
    ),
    'H' to listOf(
        "#...#",
        "#...#",
        "#...#",
        "#####",
        "#...#",
        "#...#",
        "#...#"
    ),
    'I' to listOf(
        "#####",
        "..#..",
        "..#..",
        "..#..",
        "..#..",
        "..#..",
        "#####"
    ),
    'J' to listOf(
        "..###",
        "...#.",
        "...#.",
        "...#.",
        "...#.",
        "#..#.",
        ".##.."
    ),
    'K' to listOf(
        "#...#",
        "#..#.",
        "#.#..",
        "##...",
        "#.#..",
        "#..#.",
        "#...#"
    ),
    'L' to listOf(
        "#....",
        "#....",
        "#....",
        "#....",
        "#....",
        "#....",
        "#####"
    ),
    'M' to listOf(
        "#...#",
        "##.##",
        "#.#.#",
        "#.#.#",
        "#...#",
        "#...#",
        "#...#"
    ),
    'N' to listOf(
        "#...#",
        "##..#",
        "#.#.#",
        "#..##",
        "#...#",
        "#...#",
        "#...#"
    ),
    'O' to listOf(
        ".###.",
        "#...#",
        "#...#",
        "#...#",
        "#...#",
        "#...#",
        ".###."
    ),
    'P' to listOf(
        "####.",
        "#...#",
        "#...#",
        "####.",
        "#....",
        "#....",
        "#...."
    ),
    'Q' to listOf(
        ".###.",
        "#...#",
        "#...#",
        "#...#",
        "#.#.#",
        "#..#.",
        ".##.#"
    ),
    'R' to listOf(
        "####.",
        "#...#",
        "#...#",
        "####.",
        "#.#..",
        "#..#.",
        "#...#"
    ),
    'S' to listOf(
        ".####",
        "#....",
        "#....",
        ".###.",
        "....#",
        "....#",
        "####."
    ),
    'T' to listOf(
        "#####",
        "..#..",
        "..#..",
        "..#..",
        "..#..",
        "..#..",
        "..#.."
    ),
    'U' to listOf(
        "#...#",
        "#...#",
        "#...#",
        "#...#",
        "#...#",
        "#...#",
        ".###."
    ),
    'V' to listOf(
        "#...#",
        "#...#",
        "#...#",
        "#...#",
        "#...#",
        ".#.#.",
        "..#.."
    ),
    'W' to listOf(
        "#...#",
        "#...#",
        "#...#",
        "#.#.#",
        "#.#.#",
        "##.##",
        "#...#"
    ),
    'X' to listOf(
        "#...#",
        "#...#",
        ".#.#.",
        "..#..",
        ".#.#.",
        "#...#",
        "#...#"
    ),
    'Y' to listOf(
        "#...#",
        "#...#",
        ".#.#.",
        "..#..",
        "..#..",
        "..#..",
        "..#.."
    ),
    'Z' to listOf(
        "#####",
        "....#",
        "...#.",
        "..#..",
        ".#...",
        "#....",
        "#####"
    )
)
