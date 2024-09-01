package com.hanggrian.plano

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class ResultFile(parent: File) : File(parent, "${PATTERN.format(LocalDateTime.now())}.png") {
    public companion object {
        private val PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm")
    }
}
