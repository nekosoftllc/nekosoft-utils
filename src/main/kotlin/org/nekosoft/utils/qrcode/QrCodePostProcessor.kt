package org.nekosoft.utils.qrcode

import java.awt.image.BufferedImage

interface QrCodePostProcessor {
    fun validate(options: QrCodeOptions): Boolean
    fun process(qrcodeImage: BufferedImage, options: QrCodeOptions)
}