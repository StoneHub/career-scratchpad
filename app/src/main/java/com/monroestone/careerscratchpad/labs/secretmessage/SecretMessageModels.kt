package com.monroestone.careerscratchpad.labs.secretmessage

data class SecretMessageCell(
    val x: Int,
    val y: Int,
    val value: String
)

data class SecretMessageParseResult(
    val cells: List<SecretMessageCell>,
    val warnings: List<String>
)

data class SecretMessageRenderOptions(
    val flipY: Boolean = false,
    val flipX: Boolean = false,
    val swapXY: Boolean = false,
    val blankChars: Set<String> = setOf("░")
)

data class SecretMessageDecodeResult(
    val parseResult: SecretMessageParseResult,
    val rendered: String,
    val renderedGrid: SecretMessageRenderedGrid,
    val options: SecretMessageRenderOptions
)

data class SecretMessageRenderedGrid(
    val rows: List<List<SecretMessageRenderedCell>>
) {
    val width: Int = rows.maxOfOrNull { it.size } ?: 0
    val height: Int = rows.size
}

data class SecretMessageRenderedCell(
    val value: String,
    val isBlank: Boolean
)
