package org.nekosoft.utils.qrcode

import java.awt.image.BufferedImage

interface QrCodePostProcessor {
    fun process(qrcodeImage: BufferedImage, options: QrCodeOptions)
}