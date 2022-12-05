package org.nekosoft.utils.qrcode.style.postproc

import org.nekosoft.utils.qrcode.QrCodeOptions
import org.nekosoft.utils.qrcode.QrCodePostProcessor
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class IconPostProcessor(val imagePath: String) : QrCodePostProcessor {

    val overlayImage: BufferedImage = ImageIO.read(File(imagePath))

    override fun validate(options: QrCodeOptions): Boolean = true

    override fun process(qrcodeImage: BufferedImage, options: QrCodeOptions) {
        // TODO Check sizes against error level percentage and resize image accordingly
        val posX = (qrcodeImage.width - overlayImage.width) / 2
        val posY = (qrcodeImage.height - overlayImage.height) / 2
        val g = qrcodeImage.createGraphics()
        g.drawImage(overlayImage, posX, posY, null)
    }
}
