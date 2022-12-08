package org.nekosoft.utils.qrcode

import io.github.g0dkar.qrcode.render.QRCodeGraphics

interface QrCodePostProcessor {
    fun validate(options: QrCodeOptions): Boolean

    /**
     * @return null if the original graphics object is to be used, otherwise the new graphics object
     * to be used for the QRCode
     */
    fun process(qrcodeImage: QRCodeGraphics, options: QrCodeOptions): QRCodeGraphics?
}