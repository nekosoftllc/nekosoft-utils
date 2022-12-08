package org.nekosoft.utils.qrcode.style

import io.github.g0dkar.qrcode.internals.QRCodeRegion
import io.github.g0dkar.qrcode.internals.QRCodeSquare
import io.github.g0dkar.qrcode.internals.QRCodeSquareType
import io.github.g0dkar.qrcode.render.QRCodeGraphics
import org.nekosoft.utils.qrcode.QrCodeDrawStyle
import org.nekosoft.utils.qrcode.QrCodeOptions

class RoundedDrawStyle(val radius: Int = 15, val joined: Boolean = false) : QrCodeDrawStyle {

    override fun validate(options: QrCodeOptions): Boolean = true

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
                    if (joined) {
                        when (getJoinType(cellData, rawData)) {
                            QRCodeRegion.TOP_LEFT_CORNER -> drawTopLeftCorner(canvas, options)
                            QRCodeRegion.TOP_RIGHT_CORNER -> drawTopRightCorner(canvas, options)
                            QRCodeRegion.BOTTOM_LEFT_CORNER -> drawBottomLeftCorner(canvas, options)
                            QRCodeRegion.BOTTOM_RIGHT_CORNER -> drawBottomRightCorner(canvas, options)
                            QRCodeRegion.TOP_MID -> drawTopMid(canvas, options)
                            QRCodeRegion.BOTTOM_MID -> drawBottomMid(canvas, options)
                            QRCodeRegion.RIGHT_MID -> drawRightMid(canvas, options)
                            QRCodeRegion.LEFT_MID -> drawLeftMid(canvas, options)
                            QRCodeRegion.MARGIN -> canvas.fillRect(0, 0, canvas.width, canvas.height, options.foregroundColor)
                            QRCodeRegion.CENTER -> canvas.fillRoundRect(0, 0, canvas.width, canvas.height, radius, options.foregroundColor)
                            else -> Unit
                        }
                    } else {
                        canvas.fillRoundRect(0, 0, canvas.width, canvas.height, radius, options.foregroundColor)
                    }
            }
        }

    }

    private fun getJoinType(cell: QRCodeSquare, data: Array<Array<QRCodeSquare?>>): QRCodeRegion {
        // TODO doesn't look great at the moment, but the idea is there
        val col = cell.col
        val row = cell.row
        val upIsDark = (row - 1 >= 0) && data[row - 1][col]?.dark == true
        val downIsDark = (row + 1 < data.size) && data[row + 1][col]?.dark == true
        val leftIsDark = (col - 1 >= 0) && data[row][col - 1]?.dark == true
        val rightIsDark = (col + 1 < data.size) && data[row][col + 1]?.dark == true
        var totalDark = 0
        if (upIsDark) totalDark += 1
        if (downIsDark) totalDark += 1
        if (leftIsDark) totalDark += 1
        if (rightIsDark) totalDark += 1
        if (totalDark == 0) return QRCodeRegion.CENTER
        if (totalDark > 2) return QRCodeRegion.MARGIN
        if ((upIsDark && downIsDark) || (leftIsDark && rightIsDark)) return QRCodeRegion.MARGIN
        if (upIsDark && leftIsDark) return QRCodeRegion.BOTTOM_RIGHT_CORNER
        if (upIsDark && rightIsDark) return QRCodeRegion.BOTTOM_LEFT_CORNER
        if (downIsDark && leftIsDark) return QRCodeRegion.TOP_RIGHT_CORNER
        if (downIsDark && rightIsDark) return QRCodeRegion.TOP_LEFT_CORNER
        if (upIsDark) return QRCodeRegion.BOTTOM_MID
        if (downIsDark) return QRCodeRegion.TOP_MID
        if (leftIsDark) return QRCodeRegion.RIGHT_MID
        if (rightIsDark) return QRCodeRegion.LEFT_MID
        return QRCodeRegion.UNKNOWN
    }

    private fun size(canvas: QRCodeGraphics) = canvas.width * 4
    private fun circleSize(canvas: QRCodeGraphics): Int = (canvas.width * 1.8).toInt()
    private fun sizeMid(canvas: QRCodeGraphics) = canvas.width * 2
    private fun circleSizeMid(canvas: QRCodeGraphics): Int = (canvas.width * 0.9).toInt()

    private fun drawTopMid(canvas: QRCodeGraphics, options: QrCodeOptions) {
        val size = sizeMid(canvas)
        val circleSize = circleSizeMid(canvas)
        canvas.fillRoundRect(0, 1, canvas.width, size, circleSize, options.foregroundColor)
    }

    private fun drawBottomMid(canvas: QRCodeGraphics, options: QrCodeOptions) {
        val size = sizeMid(canvas)
        val circleSize = circleSizeMid(canvas)
        canvas.fillRoundRect(0, -size + canvas.height - 1, canvas.width, size, circleSize, options.foregroundColor)
    }

    private fun drawRightMid(canvas: QRCodeGraphics, options: QrCodeOptions) {
        val size = sizeMid(canvas)
        val circleSize = circleSizeMid(canvas)
        canvas.fillRoundRect(-size + canvas.width - 1, 0, size, canvas.height, circleSize, options.foregroundColor)
    }

    private fun drawLeftMid(canvas: QRCodeGraphics, options: QrCodeOptions) {
        val size = sizeMid(canvas)
        val circleSize = circleSizeMid(canvas)
        canvas.fillRoundRect(1, 0, size, canvas.height, circleSize, options.foregroundColor)
    }

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