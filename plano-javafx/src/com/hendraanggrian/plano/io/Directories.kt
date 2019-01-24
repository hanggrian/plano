package com.hendraanggrian.plano.io

import com.hendraanggrian.plano.BuildConfig
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm")

object MainDirectory : Directory(SystemUtils.USER_HOME)

object DesktopDirectory : Directory(MainDirectory, "Desktop")

class PreferencesFile : File(MainDirectory, ".${BuildConfig.ARTIFACT}")

class ResultFile : File(DesktopDirectory, "${FORMATTER.format(LocalDateTime.now())}.png")

sealed class Directory : File {

    constructor(parent: String) : super(parent)

    constructor(parent: String, child: String) : super(parent, child)

    constructor(parent: Directory, child: String) : super(parent, child)

    init {
        @Suppress("LeakingThis") mkdirs()
    }
}