package com.hanggrian.plano

import android.os.Environment

val ResultFile.Companion.DEVICE_DOCUMENTS
    get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
