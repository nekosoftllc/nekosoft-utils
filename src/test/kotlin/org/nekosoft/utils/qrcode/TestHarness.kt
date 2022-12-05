package org.nekosoft.utils.qrcode

import org.nekosoft.utils.qrcode.style.RoundedDrawStyle
import org.nekosoft.utils.qrcode.style.postproc.IconPostProcessor
import java.io.FileOutputStream

fun main() {
    FileOutputStream("qrcode.png").use {
        val result = QrCode("https://shlink.nekosoft.org")
            .render(QrCodeOptions(
                fgColor = "#000000",
                bgColor = "#EBFDF7",
                drawStyle = RoundedDrawStyle(25, false),
                postProcessor = listOf(
                    IconPostProcessor("/Users/fedmest/Downloads/QRCode_QA.png"),
                ),
                correctionLevel = CorrectionLevel.Q,
            ))
        result.writeTo(it)
    }
}