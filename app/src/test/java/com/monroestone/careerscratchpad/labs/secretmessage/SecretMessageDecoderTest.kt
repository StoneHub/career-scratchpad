package com.monroestone.careerscratchpad.labs.secretmessage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecretMessageDecoderTest {
    private val decoder = SecretMessageDecoder()

    @Test
    fun emptyInputReturnsUsefulWarning() {
        val result = decoder.parse("")

        assertTrue(result.cells.isEmpty())
        assertTrue(result.warnings.any { it.contains("Paste coordinate data") })
    }

    @Test
    fun parsesWhitespaceRowsIntoCells() {
        val result = decoder.parse(
            """
            0 # 0
            1 # 0
            0 # 1
            """.trimIndent()
        )

        assertEquals(
            listOf(
                SecretMessageCell(x = 0, y = 0, value = "#"),
                SecretMessageCell(x = 1, y = 0, value = "#"),
                SecretMessageCell(x = 0, y = 1, value = "#"),
            ),
            result.cells
        )
        assertTrue(result.warnings.isEmpty())
    }

    @Test
    fun parsesCopiedTableLikeTriplets() {
        val result = decoder.parse("x character y 0 H 0 1 I 0 0 ! 1")

        assertEquals(
            listOf(
                SecretMessageCell(x = 0, y = 0, value = "H"),
                SecretMessageCell(x = 1, y = 0, value = "I"),
                SecretMessageCell(x = 0, y = 1, value = "!"),
            ),
            result.cells
        )
    }

    @Test
    fun parsesCopiedTableTokensAcrossLinesWithoutSpuriousWarnings() {
        val result = decoder.parse(
            """
            x
            character
            y
            0
            H
            0
            1
            I
            0
            """.trimIndent()
        )

        assertEquals(
            listOf(
                SecretMessageCell(x = 0, y = 0, value = "H"),
                SecretMessageCell(x = 1, y = 0, value = "I"),
            ),
            result.cells
        )
        assertTrue(result.warnings.isEmpty())
    }

    @Test
    fun malformedRowsAreSkippedWithWarnings() {
        val result = decoder.parse(
            """
            0 A 0
            not a row
            1 B nope
            1 B 0
            """.trimIndent()
        )

        assertEquals(
            listOf(
                SecretMessageCell(x = 0, y = 0, value = "A"),
                SecretMessageCell(x = 1, y = 0, value = "B"),
            ),
            result.cells
        )
        assertEquals(2, result.warnings.size)
    }

    @Test
    fun rendersGridWithYIncreasingUpByDefault() {
        val rendered = decoder.render(
            cells = listOf(
                SecretMessageCell(x = 0, y = 0, value = "A"),
                SecretMessageCell(x = 1, y = 0, value = "B"),
                SecretMessageCell(x = 0, y = 1, value = "C"),
            ),
            options = SecretMessageRenderOptions()
        )

        assertEquals("C \nAB", rendered)
    }

    @Test
    fun flipOptionsChangeOutputPredictably() {
        val cells = listOf(
            SecretMessageCell(x = 0, y = 0, value = "A"),
            SecretMessageCell(x = 1, y = 0, value = "B"),
            SecretMessageCell(x = 0, y = 1, value = "C"),
        )

        assertEquals("AB\nC ", decoder.render(cells, SecretMessageRenderOptions(flipY = true)))
        assertEquals(" C\nBA", decoder.render(cells, SecretMessageRenderOptions(flipX = true)))
        assertEquals("B \nAC", decoder.render(cells, SecretMessageRenderOptions(swapXY = true)))
    }

    @Test
    fun blankCharactersRenderAsSpaces() {
        val rendered = decoder.render(
            cells = listOf(
                SecretMessageCell(x = 0, y = 0, value = "█"),
                SecretMessageCell(x = 1, y = 0, value = "."),
                SecretMessageCell(x = 2, y = 0, value = "█"),
            ),
            options = SecretMessageRenderOptions(blankChars = setOf("."))
        )

        assertEquals("█ █", rendered)
        assertFalse(rendered.contains("."))
    }

    @Test
    fun shadedCellsRenderAsSpacesByDefault() {
        val rendered = decoder.render(
            cells = listOf(
                SecretMessageCell(x = 0, y = 0, value = "█"),
                SecretMessageCell(x = 1, y = 0, value = "░"),
                SecretMessageCell(x = 2, y = 0, value = "█"),
            )
        )

        assertEquals("█ █", rendered)
        assertFalse(rendered.contains("░"))
    }

    @Test
    fun renderedGridPreservesSquarePixelState() {
        val grid = decoder.renderGrid(
            cells = listOf(
                SecretMessageCell(x = 0, y = 0, value = "█"),
                SecretMessageCell(x = 1, y = 0, value = "░"),
                SecretMessageCell(x = 2, y = 0, value = "█"),
            )
        )

        assertEquals(3, grid.width)
        assertEquals(1, grid.height)
        assertEquals(false, grid.rows[0][0].isBlank)
        assertEquals(true, grid.rows[0][1].isBlank)
        assertEquals(false, grid.rows[0][2].isBlank)
    }

    @Test
    fun singleMethodCodeShowsTheInterviewShape() {
        val code = secretMessageSingleMethodCode()

        assertTrue(code.contains("fun printSecretMessage(input: String): String"))
        assertTrue(code.contains("chunked(3)"))
        assertTrue(code.contains("maxY downTo 0"))
        assertTrue(code.contains("println(output)"))
    }

    @Test
    fun generatedPlainTextTableRendersBackToBlockLetter() {
        val table = plainTextToCoordinateTable("F")
        val parsed = decoder.parse(table)

        assertEquals(35, parsed.cells.size)
        assertTrue(table.startsWith("x\tcharacter\ty\n0\t█\t6"))
        assertEquals(
            "█████\n" +
                "█    \n" +
                "█    \n" +
                "████ \n" +
                "█    \n" +
                "█    \n" +
                "█    ",
            decoder.render(parsed.cells)
        )
    }

    @Test
    fun hiExampleUsesBlockLetterTable() {
        val hiTable = secretMessageExampleTables().first { it.label == "Load HI" }.tableText
        val result = decoder.decode(hiTable)

        assertEquals(70, result.parseResult.cells.size)
        assertEquals(11, result.renderedGrid.width)
        assertEquals(7, result.renderedGrid.height)
        assertTrue(result.rendered.startsWith("█   █ █████"))
    }

    @Test
    fun pastedTokenStreamFormatsAsTableRows() {
        val formatted = formatCoordinateTableText("x character y 0 H 0 1 I 0")

        assertEquals(
            "x\tcharacter\ty\n" +
                "0\tH\t0\n" +
                "1\tI\t0",
            formatted
        )
    }

    @Test
    fun exampleTablesIncludePracticeInputs() {
        val labels = secretMessageExampleTables().map { it.label }

        assertEquals(listOf("Load HI", "Load F", "Load HCMIDBO"), labels)
    }
}
