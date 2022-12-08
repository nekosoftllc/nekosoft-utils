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

        val actualCellSize = if (options.cellSize > 0) {
            options.cellSize
        } else if (options.imageSize > 0) {
            val csz = if (options.marginSize > 0) {
                (options.imageSize.toDouble() - options.marginSize * 2.0) / rawData.size
            } else {
                options.imageSize.toDouble() / (rawData.size + 2.0)
            }
            if (options.isMaxImageSize) {
                floor(csz)
            } else {
                ceil(csz)
            }.toInt()
        } else {
            // This should never happen based on check in options object
            throw IllegalArgumentException("Must specify one and only one of image size or cell size")
        }

        val actualMarginSize = if (options.marginSize > 0) options.marginSize else actualCellSize

        val imageOut = ByteArrayOutputStream()

        val qrGraphics = (if (options.drawStyle is DefaultDrawStyle) {
            qrCode.render(
                margin = actualMarginSize,
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
