package org.nekosoft.utils.qrcode

import io.github.g0dkar.qrcode.ErrorCorrectionLevel
import io.github.g0dkar.qrcode.QRCode
import io.github.g0dkar.qrcode.QRCodeDataType
import org.nekosoft.utils.qrcode.style.DefaultDrawStyle
import java.io.ByteArrayOutputStream
import kotlin.math.ceil
import kotlin.math.floor

class QrCode(
    val data: String,
    val errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.M,
    val type: QRCodeDataType? = null
    ) {

    fun render(options: QrCodeOptions): ByteArrayOutputStream {
        val qrCode =
            if (type == null)
                QRCode(data, errorCorrectionLevel = errorCorrectionLevel)
            else
                QRCode(data, dataType = type, errorCorrectionLevel = errorCorrectionLevel)

        val rawData = qrCode.encode()

        val actualCellSize = if (options.imageSize > 0) {
            val csz = (options.imageSize.toDouble() - options.marginSize * 2) / rawData.size
            if (options.isMaxImageSize) {
                floor(csz)
            } else {
                ceil(csz)
            }.toInt()
        } else {
            options.cellSize
        }

        val imageOut = ByteArrayOutputStream()

        (if (options.drawStyle is DefaultDrawStyle) {
            qrCode.render(
                margin = options.marginSize,
                cellSize = actualCellSize,
                rawData = rawData,
                darkColor = options.foregroundColor,
                brightColor = options.backgroundColor,
                marginColor = options.marginColor,
            )
        } else {
            qrCode.renderShaded(
                margin = options.marginSize,
                cellSize = actualCellSize,
                rawData = rawData,
            ) { qrCodeSquare, qrCodeGraphics -> options.drawStyle.render(qrCodeSquare, qrCodeGraphics, rawData, options) }
        }).writeImage(imageOut)

        return imageOut
    }

}
