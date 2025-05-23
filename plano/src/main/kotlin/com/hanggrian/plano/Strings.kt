package com.hanggrian.plano

import org.apache.commons.math3.util.Precision.round
import java.math.BigDecimal

public fun Double.clean(): String = round(this, 1, BigDecimal.ROUND_HALF_DOWN).cleanInternal()

public fun Float.clean(): String = round(this, 1, BigDecimal.ROUND_HALF_DOWN).cleanInternal()

private fun Number.cleanInternal(): String {
    var s = toString()
    if (s.startsWith('-')) {
        s = s.substringAfter('-')
    }
    if (s.endsWith(".0")) {
        s = s.substringBeforeLast(".0")
    }
    return s
}
