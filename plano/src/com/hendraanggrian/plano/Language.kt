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
    EN_US(Locale.US),
    ID_ID(Locale("id", "ID"));

    /** Reverse the damage done in [Locale.convertOldISOCodes]. */
    val code: String
        get() = nativeLocale.language.let {
            when (it) {
                "iw" -> "he"
                "ji" -> "yi"
                "in" -> "id"
                else -> it
            }
        }

    val fullCode: String get() = "$code-${nativeLocale.country}"

    fun toLocale(): Locale = nativeLocale

    @JvmOverloads
    fun toString(showCurrency: Boolean = false): String = nativeLocale
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

        fun ofFullCode(fullCode: String): Language =
            find { it.fullCode == fullCode }

        private inline fun find(predicate: (Language) -> Boolean): Language =
            Language.values().singleOrNull(predicate) ?: EN_US
    }
}