package org.nekosoft.utils.qrcode

import io.github.g0dkar.qrcode.render.Colors
import org.nekosoft.utils.qrcode.style.DefaultDrawStyle

class QrCodeOptions(
    bgColor: String,
    fgColor: String,
    mgColor: String,
    mgSize: Int,
    clSize: Int,
    val imageSize: Int,
    val drawStyle: QrCodeDrawStyle,
    val postProcessors: List<QrCodePostProcessor>,
    val isMaxImageSize: Boolean,
    val correctionLevel: CorrectionLevel,
) {
    companion object {

        operator fun invoke(
            bgColor: String? = null,
            fgColor: String? = null,
            marginColor: String? = null,
            marginSize: Int? = null,
            cellSize: Int? = null,
            imageSize: Int? = null,
            drawStyle: QrCodeDrawStyle? = null,
            postProcessor: List<QrCodePostProcessor>? = null,
            isMaxImageSize: Boolean? = null,
            correctionLevel: CorrectionLevel? = null,
        ) = QrCodeOptions(
            bgColor ?: "#FFFFFFFF",
            fgColor ?: "#FF000000",
            marginColor ?: "", // "" : same as bgColor
            marginSize ?: -1, // <= 0 : same as cell size
            cellSize ?: -1, // <= 0 : calculate from image size
            imageSize ?: -1, // <= 0 : use cell size
            drawStyle ?: DefaultDrawStyle(),
            postProcessor ?: listOf(),
            isMaxImageSize ?: false,
            correctionLevel ?: CorrectionLevel.M,
        )

        fun parseColorString(color: String): Int =
            if (color.startsWith("#") && color.length == 7) {
                Colors.css(color)
            } else if (color.startsWith("#") && color.length == 9) {
                Colors.withAlpha(color.substring(3..8).toInt(16), color.substring(1..2).toInt(16))
            } else
                Colors.allColors()[color] ?: throw IllegalArgumentException(color)

    }

    val backgroundColor: Int
    val foregroundColor: Int
    val marginColor: Int
    val marginSize: Int
    val cellSize: Int

    init {
        backgroundColor = parseColorString(bgColor)
        foregroundColor = parseColorString(fgColor)
        marginColor = if (mgColor.isBlank()) backgroundColor else parseColorString(mgColor)
        cellSize = if (clSize <= 0 && imageSize <= 0) {
            25
        } else if (!((clSize <= 0) xor (imageSize <= 0))) {
            throw IllegalArgumentException("Must specify one and only one of image size or cell size")
        } else {
            clSize
        }
        marginSize = if (mgSize <= 0) if (cellSize > 0) cellSize else 0 else mgSize
    }

}
