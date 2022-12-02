package org.nekosoft.utils.qrcode.style

import io.github.g0dkar.qrcode.internals.QRCodeRegion
import io.github.g0dkar.qrcode.internals.QRCodeSquare
import io.github.g0dkar.qrcode.internals.QRCodeSquareType
import io.github.g0dkar.qrcode.render.QRCodeGraphics
import org.nekosoft.utils.qrcode.QrCodeDrawStyle
import org.nekosoft.utils.qrcode.QrCodeOptions

class RoundedDrawStyle(val radius: Int = 15, val joined: Boolean = false) : QrCodeDrawStyle {

    override fun render(
        cellData: QRCodeSquare,
        canvas: QRCodeGraphics,
        rawData: Array<Array<QRCodeSquare?>>,
        options: QrCodeOptions
    ) {
        // Always paint it white to make sure there are no transparent pixels
        canvas.fill(options.backgroundColor)

        if (cellData.dark) {
            when (cellData.squareInfo.type) {
                QRCodeSquareType.POSITION_PROBE ->
                    when (cellData.squareInfo.region) {
                        QRCodeRegion.TOP_LEFT_CORNER -> drawTopLeftCorner(canvas, options)
                        QRCodeRegion.TOP_RIGHT_CORNER -> drawTopRightCorner(canvas, options)
                        QRCodeRegion.BOTTOM_LEFT_CORNER -> drawBottomLeftCorner(canvas, options)
                        QRCodeRegion.BOTTOM_RIGHT_CORNER -> drawBottomRightCorner(canvas, options)
                        else -> canvas.fill(options.foregroundColor)
                    }
                QRCodeSquareType.MARGIN ->
                    canvas.fill(options.marginColor)
                else ->
                    if (joined && isJoiner(cellData, rawData)) {
                        canvas.fillRect(0, 0, canvas.width, canvas.height, options.foregroundColor)
                    } else {
                        canvas.fillRoundRect(0, 0, canvas.width, canvas.height, radius, options.foregroundColor)
                    }
            }
        }

    }

    private fun isJoiner(cell: QRCodeSquare, data: Array<Array<QRCodeSquare?>>): Boolean {
        // TODO doesn't look great at the moment, but the idea is there
        val col = cell.col
        val row = cell.row
        if (col - 1 < 0 || row - 1 < 0 || row + 1 >= data.size || col + 1 >= data[0].size) {
            return false
        }
        var totJoinedCells = 0
        if (data[row + 1][col + 1]?.dark == true) {
            totJoinedCells += 1
        }
        if (data[row + 1][col - 1]?.dark == true) {
            totJoinedCells += 1
        }
        if (data[row - 1][col + 1]?.dark == true) {
            totJoinedCells += 1
        }
        if (data[row - 1][col - 1]?.dark == true) {
            totJoinedCells += 1
        }
        return totJoinedCells > 1
    }

    private fun size(canvas: QRCodeGraphics) = canvas.width * 4
    private fun circleSize(canvas: QRCodeGraphics): Int = (canvas.width * 1.8).toInt()

    private fun drawTopLeftCorner(canvas: QRCodeGraphics, options: QrCodeOptions) {
        val size = size(canvas)
        val circleSize = circleSize(canvas)
        canvas.fillRoundRect(0, 0, size, size, circleSize, options.foregroundColor)
    }

    private fun drawTopRightCorner(canvas: QRCodeGraphics, options: QrCodeOptions) {
        val size = size(canvas)
        val circleSize = circleSize(canvas)
        canvas.fillRoundRect(-size + canvas.width, 0, size, size, circleSize, options.foregroundColor)
    }

    private fun drawBottomLeftCorner(canvas: QRCodeGraphics, options: QrCodeOptions) {
        val size = size(canvas)
        val circleSize = circleSize(canvas)
        canvas.fillRoundRect(0, -size + canvas.width, size, size, circleSize, options.foregroundColor)
    }

    private fun drawBottomRightCorner(canvas: QRCodeGraphics, options: QrCodeOptions) {
        val size = size(canvas)
        val circleSize = circleSize(canvas)
        canvas.fillRoundRect(-size + canvas.width, -size + canvas.width, size, size, circleSize, options.foregroundColor)
    }

}