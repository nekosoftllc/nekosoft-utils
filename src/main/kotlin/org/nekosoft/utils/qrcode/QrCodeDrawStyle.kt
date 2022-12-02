package org.nekosoft.utils.qrcode

import io.github.g0dkar.qrcode.internals.QRCodeSquare
import io.github.g0dkar.qrcode.render.QRCodeGraphics

interface QrCodeDrawStyle {
    fun render(cellData: QRCodeSquare, canvas: QRCodeGraphics, rawData: Array<Array<QRCodeSquare?>>, options: QrCodeOptions)
}