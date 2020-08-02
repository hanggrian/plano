package com.hendraanggrian.plano.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openUrl(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))