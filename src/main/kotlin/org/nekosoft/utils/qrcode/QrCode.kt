package org.nekosoft.utils.qrcode

import io.github.g0dkar.qrcode.QRCode
import io.github.g0dkar.qrcode.QRCodeDataType
import org.nekosoft.utils.qrcode.style.DefaultDrawStyle
import java.io.ByteArrayOutputStream
import kotlin.math.ceil
import kotlin.math.floor

class QrCode(
    val data: String,
    val type: QRCodeDataType? = null
    ) {

    fun render(options: QrCodeOptions): ByteArrayOutputStream {
        // Validate draw style and post processors
        if (!options.drawStyle.validate(options)) throw IllegalArgumentException("Invalid QR Code options for the selected drawing style ${options.drawStyle.javaClass.name}")
        options.postProcessors.forEach {
            if (!it.validate(options)) throw IllegalArgumentException("Invalid QR Code options for the selected post processor ${it.javaClass.name}")
        }

        val qrCode =
            if (type == null)
                QRCode(data, errorCorrectionLevel = options.correctionLevel.errorCorrectionLevel)
            else
                QRCode(data, dataType = type, errorCorrectionLevel = options.correctionLevel.errorCorrectionLevel)

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

        val qrGraphics = (if (options.drawStyle is DefaultDrawStyle) {
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
        })

        // Returns a BufferedImage instance on JVM platform
        var finalQRCodeGraphics = qrGraphics
        for (p in options.postProcessors) {
            p.process(finalQRCodeGraphics, options)?.let { finalQRCodeGraphics = it }
        }

        finalQRCodeGraphics.writeImage(imageOut)
        return imageOut
    }

}
