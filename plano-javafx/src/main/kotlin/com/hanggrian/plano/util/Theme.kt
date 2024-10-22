package com.hanggrian.plano.util

import org.apache.commons.lang3.SystemUtils
import java.util.concurrent.TimeUnit
import java.util.prefs.Preferences

const val THEME_SYSTEM = "System"
const val THEME_LIGHT = "Light"
const val THEME_DARK = "Dark"

fun isDarkTheme(theme: String): Boolean =
    when (theme) {
        THEME_SYSTEM ->
            runCatching {
                when {
                    SystemUtils.IS_OS_MAC_OSX -> {
                        val process =
                            ProcessBuilder("defaults", "read", "-g", "AppleInterfaceStyle").start()
                        val result =
                            process.inputStream
                                .reader(Charsets.UTF_8)
                                .use { it.readText() }
                        process.waitFor(3, TimeUnit.SECONDS)
                        THEME_DARK in result
                    }
                    SystemUtils.IS_OS_WINDOWS_10 ->
                        Preferences
                            .userRoot()
                            .node("Software")
                            .node("Microsoft")
                            .node("Windows")
                            .node("CurrentVersion")
                            .node("Themes")
                            .node("Personalize")
                            .getBoolean("AppsUseLightTheme", false)
                            .not()
                    else -> false
                }
            }.getOrDefault(false)
        THEME_LIGHT -> false
        THEME_DARK -> true
        else -> error("Unrecognized theme: $theme")
    }
