package com.hanggrian.plano

import org.apache.commons.lang3.SystemUtils
import java.io.File

val ResultFile.Companion.HOME_DOCUMENTS
    get() = File(SystemUtils.USER_HOME).resolve("Documents")
