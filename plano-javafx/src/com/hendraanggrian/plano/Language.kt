package com.hendraanggrian.plano

import java.util.Currency
import java.util.Locale
import java.util.ResourceBundle

/**
 * List of supported languages and their JVM implementation.
 * There's a separate module for this enum since it is unused in Android.
 *
 * @param nativeLocale following pattern of [Regex.nativePattern]
 */
enum class Language(private val nativeLocale: Locale) {
    ENGLISH(Locale.ENGLISH),
    INDONESIAN(Locale("id"));

    val code: String get() = nativeLocale.language

    fun toLocale(): Locale = nativeLocale

    fun asString(showCurrency: Boolean = false): String = nativeLocale
        .getDisplayLanguage(nativeLocale)
        .let {
            when {
                showCurrency -> "${Currency.getInstance(nativeLocale).symbol} - $it"
                else -> it
            }
        }

    fun toResourcesBundle(): ResourceBundle = ResourceBundle.getBundle("string_$code")

    companion object {

        fun ofDisplay(name: String): Language =
            find { it.toLocale().displayLanguage == name }

        fun ofCode(code: String): Language =
            find { it.code == code }

        private inline fun find(predicate: (Language) -> Boolean): Language =
            values().singleOrNull(predicate) ?: ENGLISH
    }
}
