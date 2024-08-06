package com.hanggrian.plano

public data class License(val name: String, val url: String) {
    public companion object {
        public fun listAll(vararg additionalLicenses: Pair<String, String>): List<License> {
            val licenses =
                mutableListOf(
                    License(
                        "Kotlin Programming Language",
                        "https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt",
                    ),
                    License(
                        "Kotlin Coroutines",
                        "https://github.com/Kotlin/kotlinx.coroutines/blob/master/LICENSE.txt",
                    ),
                    License(
                        "Ktor Asynchronous Web Framework",
                        "https://github.com/ktorio/ktor/blob/master/LICENSE",
                    ),
                    License(
                        "Apache Commons Math",
                        "https://github.com/apache/commons-math/blob/master/LICENSE.txt",
                    ),
                )
            licenses += additionalLicenses.map { License(it.first, it.second) }
            return licenses.sortedBy { it.name }
        }
    }
}
