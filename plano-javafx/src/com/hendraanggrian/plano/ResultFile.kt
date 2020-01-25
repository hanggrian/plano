package com.hendraanggrian.plano

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.apache.commons.lang3.SystemUtils

class ResultFile : File(PARENT, "${DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm").format(LocalDateTime.now())}.png") {

    private companion object {
        val PARENT = File(SystemUtils.USER_HOME).resolve("Desktop")
    }
}
