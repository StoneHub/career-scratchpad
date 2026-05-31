package com.monroestone.careerscratchpad.labs.secretmessage

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.monroestone.careerscratchpad.lab.Lab
import com.monroestone.careerscratchpad.lab.LabCategory
import com.monroestone.careerscratchpad.ui.CareerScratchpadTheme

class SecretMessageLab : Lab {
    override val id: String = "secret-message-decoder"
    override val title: String = "Secret Message Decoder"
    override val category: LabCategory = LabCategory.DATA_PARSING

    private val decoder = SecretMessageDecoder()

    @Composable
    override fun Content() {
        SecretMessageLabScreen(decoder = decoder)
    }
}

@Composable
private fun SecretMessageLabScreen(
    decoder: SecretMessageDecoder,
    initialInput: String = ""
) {
    var input by remember { mutableStateOf(initialInput) }
    var plainTextInput by remember { mutableStateOf("") }
    var result by remember {
        mutableStateOf(
            if (initialInput.isBlank()) {
                null
            } else {
                decoder.decode(
                    text = initialInput,
                    options = SecretMessageRenderOptions()
                )
            }
        )
    }
    fun decodeText(text: String) {
        result = decoder.decode(
            text = text,
            options = SecretMessageRenderOptions()
        )
    }

    fun decodeInput() {
        decodeText(input)
    }

    fun loadTable(tableText: String) {
        val formattedTable = formatCoordinateTableText(tableText)
        input = formattedTable
        decodeText(formattedTable)
    }

    fun generateTableFromPlainText() {
        val tableText = plainTextToCoordinateTable(plainTextInput)
        input = tableText
        decodeText(tableText)
    }

    val inputContent: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            InputPanel(
                input = input,
                onInputChange = { input = formatCoordinateTableText(it) },
                exampleTables = secretMessageExampleTables(),
                onLoadExample = { tableText -> loadTable(tableText) },
                plainText = plainTextInput,
                onPlainTextChange = { plainTextInput = it },
                onPlainTextSubmit = { generateTableFromPlainText() },
                onDecode = { decodeInput() }
            )
        }
    }
    val outputContent: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutputPanel(result = result)
        }
    }

    BoxWithConstraints(modifier = Modifier.padding(16.dp)) {
        if (maxWidth >= 480.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 720.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    inputContent()
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 720.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    outputContent()
                }
            }
        } else {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                inputContent()
                outputContent()
            }
        }
    }
}

@Composable
private fun InputPanel(
    input: String,
    onInputChange: (String) -> Unit,
    exampleTables: List<SecretMessageExampleTable>,
    onLoadExample: (String) -> Unit,
    plainText: String,
    onPlainTextChange: (String) -> Unit,
    onPlainTextSubmit: () -> Unit,
    onDecode: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = "Input",
                subtitle = "Paste copied table text or rows like 0 H 0."
            )
            QuickLoadButtons(
                exampleTables = exampleTables,
                onLoadExample = onLoadExample
            )
            SectionHeader(
                title = "Plain text to table",
                subtitle = "Press return to replace the coordinate table and update the preview."
            )
            OutlinedTextField(
                value = plainText,
                onValueChange = onPlainTextChange,
                label = { Text("Message A-Z") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onPlainTextSubmit()
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()
            Text(
                text = "Coordinate data",
                style = MaterialTheme.typography.titleSmall
            )
            Button(
                onClick = onDecode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Decode coordinate data")
            }
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                label = { Text("x    character    y") },
                minLines = 12,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            )
            Text(
                text = "Paste or generate x character y data. One token per line is displayed as a table-like column stack.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickLoadButtons(
    exampleTables: List<SecretMessageExampleTable>,
    onLoadExample: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        exampleTables.forEach { example ->
            Button(onClick = { onLoadExample(example.tableText) }) {
                Text(example.label)
            }
        }
    }
}

@Composable
private fun OutputPanel(result: SecretMessageDecodeResult?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = "Output",
                subtitle = "Read wide messages left to right; scroll sideways instead of rotating."
            )

            if (result == null) {
                EmptyOutput()
            } else {
                DecodeResultView(result)
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyOutput() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            text = "Decoded output will appear here after you paste coordinate data and press Decode.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DecodeResultView(result: SecretMessageDecodeResult) {
    val parseResult = result.parseResult
    val outputSize = if (result.rendered.isBlank()) {
        "0 x 0"
    } else {
        val rows = result.rendered.lines()
        val width = rows.maxOfOrNull { it.length } ?: 0
        "${width} x ${rows.size}"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBlock(label = "Cells", value = parseResult.cells.size.toString(), modifier = Modifier.weight(1f))
        StatBlock(label = "Warnings", value = parseResult.warnings.size.toString(), modifier = Modifier.weight(1f))
        StatBlock(label = "Grid", value = outputSize, modifier = Modifier.weight(1f))
    }

    Text(
        text = "Square pixel preview",
        style = MaterialTheme.typography.titleSmall
    )
    PixelGridView(grid = result.renderedGrid, maxCellSize = 14.dp)

    Text(
        text = "Single-method Kotlin version",
        style = MaterialTheme.typography.titleSmall
    )
    SingleMethodCodeView()

    if (parseResult.warnings.isNotEmpty()) {
        WarningList(parseResult.warnings)
    }
}

@Composable
private fun PixelGridView(
    grid: SecretMessageRenderedGrid,
    maxCellSize: Dp = 12.dp,
    minCellSize: Dp = 3.dp
) {
    if (grid.width == 0 || grid.height == 0) return

    val filledColor = MaterialTheme.colorScheme.onSurface
    val blankColor = Color.Transparent

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        val availableWidth = (maxWidth - 32.dp).coerceAtLeast(minCellSize)
        val fittedCellSize = (availableWidth.value / grid.width)
            .coerceIn(minCellSize.value, maxCellSize.value)
            .dp

        PixelGridCanvas(
            grid = grid,
            cellSize = fittedCellSize,
            filledColor = filledColor,
            blankColor = blankColor
        )
    }
}

@Composable
private fun PixelGridCanvas(
    grid: SecretMessageRenderedGrid,
    cellSize: Dp,
    filledColor: Color,
    blankColor: Color
) {
    Canvas(
        modifier = Modifier
            .width((cellSize.value * grid.width).dp)
            .height((cellSize.value * grid.height).dp)
    ) {
        val cellPx = cellSize.toPx()
        grid.rows.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, cell ->
                drawRect(
                    color = if (cell.isBlank) blankColor else filledColor,
                    topLeft = Offset(
                        x = columnIndex * cellPx,
                        y = rowIndex * cellPx
                    ),
                    size = Size(cellPx, cellPx)
                )
            }
        }
    }
}

@Composable
private fun SingleMethodCodeView() {
    SelectionContainer {
        Text(
            text = secretMessageSingleMethodCode(),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            softWrap = false
        )
    }
}

@Composable
private fun StatBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun WarningList(warnings: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Warnings",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error
        )
        warnings.forEach { warning ->
            Text(
                text = warning,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

private val sampleInput = """
    x
    character
    y
    0
    H
    0
    1
    I
    0
    0
    !
    1
""".trimIndent()

private val wideMessagePreviewInput = """
    x
    character
    y
    0
    █
    0
    1
    █
    0
    2
    ░
    0
    3
    ░
    0
    4
    █
    0
    0
    █
    1
    1
    ░
    1
    2
    ░
    1
    3
    ░
    1
    4
    █
    1
    0
    █
    2
    1
    █
    2
    2
    █
    2
    3
    █
    2
    4
    █
    2
""".trimIndent()

@Preview(showBackground = true)
@Composable
private fun SecretMessageLabEmptyPreview() {
    CareerScratchpadTheme {
        SecretMessageLabScreen(decoder = SecretMessageDecoder())
    }
}

@Preview(showBackground = true)
@Composable
private fun SecretMessageLabWithOutputPreview() {
    CareerScratchpadTheme {
        SecretMessageLabScreen(
            decoder = SecretMessageDecoder(),
            initialInput = sampleInput
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SecretMessageWideOutputPreview() {
    CareerScratchpadTheme {
        SecretMessageLabScreen(
            decoder = SecretMessageDecoder(),
            initialInput = wideMessagePreviewInput
        )
    }
}

@Preview(showBackground = true, widthDp = 700, heightDp = 420)
@Composable
private fun SecretMessageFoldableBookPreview() {
    CareerScratchpadTheme {
        SecretMessageLabScreen(
            decoder = SecretMessageDecoder(),
            initialInput = plainTextToCoordinateTable("HI")
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SecretMessageLabWarningsPreview() {
    CareerScratchpadTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            OutputPanel(
                result = SecretMessageDecodeResult(
                    parseResult = SecretMessageParseResult(
                        cells = listOf(
                            SecretMessageCell(x = 0, y = 0, value = "H"),
                            SecretMessageCell(x = 1, y = 0, value = "I")
                        ),
                        warnings = listOf("Line 4: expected x character y, got \"not a row\".")
                    ),
                    rendered = "HI",
                    renderedGrid = SecretMessageRenderedGrid(
                        rows = listOf(
                            listOf(
                                SecretMessageRenderedCell("H", isBlank = false),
                                SecretMessageRenderedCell("I", isBlank = false)
                            )
                        )
                    ),
                    options = SecretMessageRenderOptions()
                )
            )
        }
    }
}
