package org.nekosoft.utils.qrcode.style.postproc

import io.github.g0dkar.qrcode.render.QRCodeGraphics
import org.nekosoft.utils.qrcode.QrCodeOptions
import org.nekosoft.utils.qrcode.QrCodePostProcessor
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class IconPostProcessor(val imagePath: String) : QrCodePostProcessor {

    val overlayImage: BufferedImage = ImageIO.read(File(imagePath))

    override fun validate(options: QrCodeOptions): Boolean {
        // TODO Check sizes against error level percentage and resize image accordingly
        return true
    }

    override fun process(qrcodeImage: QRCodeGraphics, options: QrCodeOptions): QRCodeGraphics? {
        val posX = (qrcodeImage.width - overlayImage.width) / 2
        val posY = (qrcodeImage.height - overlayImage.height) / 2
        val g = (qrcodeImage.nativeImage() as BufferedImage).createGraphics()
        g.drawImage(overlayImage, posX, posY, null)
        g.dispose()
        return null
    }

}
