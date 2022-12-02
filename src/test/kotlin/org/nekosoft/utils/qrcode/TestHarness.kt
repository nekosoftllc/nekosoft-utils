package org.nekosoft.utils.qrcode

import org.nekosoft.utils.qrcode.style.RoundedDrawStyle
import java.io.FileOutputStream

fun main() {
    FileOutputStream("qrcode.png").use {
        val result = QrCode("https://shlink.nekosoft.org")
            .render(QrCodeOptions(
                drawStyle = RoundedDrawStyle(25, true)
            ))
        result.writeTo(it)
    }
}