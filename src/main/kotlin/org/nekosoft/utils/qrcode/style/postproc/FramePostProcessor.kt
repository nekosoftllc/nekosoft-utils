package org.nekosoft.utils.qrcode.style.postproc

import io.github.g0dkar.qrcode.render.DefaultQRCodeGraphicsFactory
import io.github.g0dkar.qrcode.render.QRCodeGraphics
import org.nekosoft.utils.qrcode.QrCodeOptions
import org.nekosoft.utils.qrcode.QrCodePostProcessor
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage

class FramePostProcessor(
    val caption: String,
    val backgroundColor: String? = null,
    val foregroundColor: String? = null,
    val fontName: String = "Arial",
): QrCodePostProcessor {

    private val bg: Int? = backgroundColor?.let { QrCodeOptions.parseColorString(it) }
    private val fg: Int? = foregroundColor?.let { QrCodeOptions.parseColorString(it) }

    override fun validate(options: QrCodeOptions): Boolean = true

    override fun process(qrcodeImage: QRCodeGraphics, options: QrCodeOptions): QRCodeGraphics {
        val actualBg = bg ?: options.backgroundColor
        val actualFg = fg ?: options.foregroundColor
        val baseWidth = qrcodeImage.width
        val innerSquareSide = (baseWidth * 1.058).toInt()
        val innerArcRadius = (baseWidth * 0.086).toInt()
        val innerSquareXY = (baseWidth * 0.044).toInt()
        val outerRectBase = (baseWidth * 1.155).toInt()
        val outerRectHeight = (baseWidth * 1.351).toInt()
        val outerArcRadius = (baseWidth * 0.110).toInt()
        val qrcodeImageXY = (baseWidth * 0.075).toInt()
        val qrGraphics = DefaultQRCodeGraphicsFactory().newGraphics(outerRectBase, outerRectHeight)
        qrGraphics.fillRoundRect(0, 0, outerRectBase, outerRectHeight, outerArcRadius, actualFg)
        qrGraphics.fillRoundRect(innerSquareXY, innerSquareXY, innerSquareSide, innerSquareSide, innerArcRadius, actualBg)
        qrGraphics.drawImage(qrcodeImage, qrcodeImageXY, qrcodeImageXY)
        val g = (qrGraphics.nativeImage() as BufferedImage).createGraphics()
        g.font = Font(fontName, Font.PLAIN, (baseWidth * 0.139).toInt())
        val fm = g.fontMetrics
        val captionX = (qrcodeImageXY + (baseWidth - fm.stringWidth(caption)) / 2.0).toInt()
        val captionY = (baseWidth * 1.263).toInt()
        g.color = Color(actualBg, true)
        g.drawString(caption, captionX, captionY)
        return qrGraphics
    }

}
