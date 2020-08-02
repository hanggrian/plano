package com.hendraanggrian.plano

data class License(
    val name: String,
    val url: String
) {
    companion object {
        fun listAll(vararg additionalLicenses: Pair<String, String>): List<License> {
            val licenses = mutableListOf(
                License(
                    "Kotlin Programming Language",
                    "https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt"
                ),
                License(
                    "Ktor Asynchronous Web Framework",
                    "https://github.com/ktorio/ktor/blob/master/LICENSE"
                ),
                License(
                    "Apache Commons Math",
                    "https://github.com/apache/commons-math/blob/master/LICENSE.txt"
                ),
                License(
                    "Prefy",
                    "https://github.com/hendraanggrian/prefy/blob/master/LICENSE"
                )
            )
            licenses += additionalLicenses.map { License(it.first, it.second) }
            return licenses.sortedBy { it.name }
        }
    }
}