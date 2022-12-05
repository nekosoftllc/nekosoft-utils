package org.nekosoft.utils.qrcode

import io.github.g0dkar.qrcode.ErrorCorrectionLevel

enum class CorrectionLevel(val ratio: Double, val errorCorrectionLevel: ErrorCorrectionLevel) {
    L(0.07, ErrorCorrectionLevel.L), M(0.15, ErrorCorrectionLevel.M), Q(0.25, ErrorCorrectionLevel.Q), H(0.30, ErrorCorrectionLevel.H)
}
