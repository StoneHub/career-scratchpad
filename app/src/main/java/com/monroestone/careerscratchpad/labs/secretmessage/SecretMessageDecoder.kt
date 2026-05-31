package com.monroestone.careerscratchpad.labs.secretmessage

class SecretMessageDecoder {
    fun parse(text: String): SecretMessageParseResult {
        if (text.isBlank()) {
            return SecretMessageParseResult(
                cells = emptyList(),
                warnings = listOf("Paste coordinate data before decoding.")
            )
        }

        val cells = mutableListOf<SecretMessageCell>()
        val warnings = mutableListOf<String>()
        val meaningfulLines = text.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        meaningfulLines.forEachIndexed { index, line ->
            val parsed = parseLine(line)
            when {
                parsed != null -> cells += parsed
                line.looksLikeHeader() -> Unit
                line.split(Regex("\\s+")).size <= 3 -> {
                    warnings += "Line ${index + 1}: expected x character y, got \"$line\"."
                }
            }
        }

        if (cells.isEmpty()) {
            val tokenStreamCells = parseTokenStream(text)
            if (tokenStreamCells.isNotEmpty()) {
                cells += tokenStreamCells
                warnings.clear()
            }
        }

        if (cells.isEmpty() && warnings.isEmpty()) {
            warnings += "No coordinate rows found. Paste rows like: 0 █ 0"
        }

        return SecretMessageParseResult(
            cells = cells,
            warnings = warnings
        )
    }

    fun render(
        cells: List<SecretMessageCell>,
        options: SecretMessageRenderOptions = SecretMessageRenderOptions()
    ): String {
        return renderGrid(cells, options).rows.joinToString("\n") { row ->
            row.joinToString(separator = "") { cell ->
                if (cell.isBlank) " " else cell.value.take(1)
            }
        }
    }

    fun renderGrid(
        cells: List<SecretMessageCell>,
        options: SecretMessageRenderOptions = SecretMessageRenderOptions()
    ): SecretMessageRenderedGrid {
        if (cells.isEmpty()) return SecretMessageRenderedGrid(emptyList())

        val transformed = cells.map { cell ->
            if (options.swapXY) {
                cell.copy(x = cell.y, y = cell.x)
            } else {
                cell
            }
        }

        val minX = transformed.minOf { it.x }
        val maxX = transformed.maxOf { it.x }
        val minY = transformed.minOf { it.y }
        val maxY = transformed.maxOf { it.y }
        val columns = if (options.flipX) maxX downTo minX else minX..maxX
        val rows = if (options.flipY) minY..maxY else maxY downTo minY
        val lookup = transformed.associateBy { it.x to it.y }

        return SecretMessageRenderedGrid(
            rows = rows.map { y ->
                columns.map { x ->
                    val value = lookup[x to y]?.value ?: " "
                    SecretMessageRenderedCell(
                        value = value.take(1),
                        isBlank = value in options.blankChars || value == " "
                    )
                }
            }
        )
    }

    fun decode(
        text: String,
        options: SecretMessageRenderOptions = SecretMessageRenderOptions()
    ): SecretMessageDecodeResult {
        val parseResult = parse(text)
        val renderedGrid = renderGrid(parseResult.cells, options)
        return SecretMessageDecodeResult(
            parseResult = parseResult,
            rendered = renderedGrid.rows.joinToString("\n") { row ->
                row.joinToString(separator = "") { cell ->
                    if (cell.isBlank) " " else cell.value
                }
            },
            renderedGrid = renderedGrid,
            options = options
        )
    }

    private fun parseLine(line: String): SecretMessageCell? {
        val parts = line.split(Regex("\\s+"))
        if (parts.size != 3) return null

        val x = parts[0].toIntOrNull() ?: return null
        val y = parts[2].toIntOrNull() ?: return null
        return SecretMessageCell(x = x, y = y, value = parts[1])
    }

    private fun parseTokenStream(text: String): List<SecretMessageCell> {
        val tokens = text.split(Regex("\\s+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .dropHeaderTokens()

        if (tokens.size < 3) return emptyList()

        return tokens.chunked(3).mapNotNull { chunk ->
            if (chunk.size != 3) {
                null
            } else {
                val x = chunk[0].toIntOrNull()
                val y = chunk[2].toIntOrNull()
                if (x == null || y == null) {
                    null
                } else {
                    SecretMessageCell(x = x, y = y, value = chunk[1])
                }
            }
        }
    }

    private fun List<String>.dropHeaderTokens(): List<String> {
        val firstThree = take(3).map { it.lowercase() }
        return if (
            firstThree.size == 3 &&
            firstThree[0] == "x" &&
            firstThree[1] in setOf("char", "character", "value") &&
            firstThree[2] == "y"
        ) {
            drop(3)
        } else {
            this
        }
    }

    private fun String.looksLikeHeader(): Boolean {
        val normalized = lowercase()
        return normalized == "x character y" ||
            normalized == "x char y" ||
            normalized == "x value y"
    }
}
