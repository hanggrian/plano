import java.util.Locale

group = RELEASE_GROUP

plugins {
    `java-library`
    kotlin("jvm")
    hendraanggrian("locale")
}

sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("res")
    }
    get("test").java.srcDir("tests/src")
}

val configuration = configurations.register("ktlint")

dependencies {
    api(kotlin("stdlib", VERSION_KOTLIN))
    api(kotlinx("coroutines-core", VERSION_COROUTINES))
    api(ktor("client-okhttp"))
    api(ktor("client-gson"))
    api(apache("maven-artifact", VERSION_MAVEN))

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
    testImplementation(apache("commons-math3", VERSION_COMMONS_MATH))
    testImplementation(google("truth", "truth", VERSION_TRUTH))

    configuration {
        invoke(ktlint())
    }
}

tasks {
    val ktlint by registering(JavaExec::class) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        inputs.dir("src")
        outputs.dir("src")
        description = "Check Kotlin code style."
        classpath(configuration.get())
        main = "com.pinterest.ktlint.Main"
        args("src/**/*.kt")
    }
    "check" {
        dependsOn(ktlint.get())
    }
    register<JavaExec>("ktlintFormat") {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath(configuration.get())
        main = "com.pinterest.ktlint.Main"
        args("-F", "src/**/*.kt")
    }

    withType<com.hendraanggrian.locale.LocalizeJavaTask> {
        resourceName = "string"
        outputDirectory = "../$RELEASE_ARTIFACT-javafx/res"
    }
    withType<com.hendraanggrian.locale.LocalizeAndroidTask> {
        defaultLocale = Locale.ENGLISH
        outputDirectory = "../$RELEASE_ARTIFACT-android/res"
    }
}

locale {
    "minimize" {
        en = "Minimize"
        id = "Perkecil"
    }
    "zoom" {
        en = "Zoom"
        id = "Perbesar"
    }
    "clear" {
        en = "Clear"
        id = "Hapus semua"
    }
    "_boxes_cleared" {
        en = "Boxes cleared."
        id = "Konten terhapus."
    }
    "expand" {
        en = "Expand"
        id = "Memperluas"
    }
    "shrink" {
        en = "Shrink"
        id = "Menyusut"
    }
    "fill_background" {
        en = "Fill background"
        id = "Isi latar belakang"
    }
    "unfill_background" {
        en = "Unfill background"
        id = "Kosongkan latar belakang"
    }
    "thicken_border" {
        en = "Thicken border"
        id = "Perbesar batasan"
    }
    "unthicken_border" {
        en = "Unthicken border"
        id = "Perkecil batasan"
    }
    "reset" {
        en = "Reset"
        id = "Atur ulang"
    }
    "theme" {
        en = "Theme"
        id = "Tema"
    }
    "use_system_appearance" {
        en = "Use system appearance"
        id = "Gunakan tampilan sistem"
    }
    "dark" {
        en = "Dark"
        id = "Gelap"
    }
    "light" {
        en = "Light"
        id = "Terang"
    }
    "language" {
        en = "Language"
        id = "Bahasa"
    }
    "about" {
        en = "About"
        id = "Tentang"
    }
    "check_for_update" {
        en = "Check for update"
        id = "Cek pembaruan"
    }
    "_update_available" {
        en = "Plano version %s is available."
        id = "Plano versi %s tersedia."
    }
    "_update_unavailable" {
        en = "You already have the latest version."
        id = "Tidak ada pembaruan."
    }
    "_desc" {
        en = "Calculate how many trim boxes fit in a media box"
        id = "Hitung berapa banyak kotak potong dalam kotak media"
    }
    "media_box" {
        en = "Media box"
        id = "Kotak media"
    }
    "_media_box" {
        en = "Source paper size, a direct parent of trim size."
        id = "Sumber ukuran kertas, induk langsung dari ukuran potong."
    }
    "trim_box" {
        en = "Trim box"
        id = "Kotak potong"
    }
    "_trim_box" {
        en = "Target size of which the paper will be trimmed into."
        id = "Ukuran target kertas yang akan dipotong."
    }
    "bleed" {
        en = "Bleed"
        id = "Lebihan"
    }
    "_bleed" {
        en =
            "Bleed is distributed around trim boxes. It is an extra area in the paper that can be safely trimmed."
        id =
            "Lebihan disebarkan di sekitar kotang potong. Lebihan adalah area ekstra di kertas yang dapat dipotong secara aman."
    }
    "allow_flip" {
        en = "Allow flip"
        id = "Izinkan putar"
    }
    "_allow_flip" {
        en =
            "When activated, trim boxes may be flipped in order to achieve highest number of trim boxes available."
        id =
            "Ketika dinyalakan, kotak potong bisa dimiringkan untuk mencapai jumlah kotak potong tertinggi."
    }
    "calculate" {
        en = "Calculate"
        id = "Kalkulasi"
    }
    "more" {
        en = "More"
        id = "Lain-lain"
    }
    "info" {
        en = "Info"
        id = "Jelaskan"
    }
    "rotate" {
        en = "Rotate"
        id = "Putar"
    }
    "a_series" {
        en = "A Series"
        id = "Seri A"
    }
    "b_series" {
        en = "B Series"
        id = "Seri B"
    }
    "c_series" {
        en = "C Series"
        id = "Seri C"
    }
    "others" {
        en = "Others"
        id = "Lain-lain"
    }
    "save" {
        en = "Save"
        id = "Simpan"
    }
    "delete" {
        en = "Delete"
        id = "Hapus"
    }
    "_save" {
        en = "Saved as %s"
        id = "Terimpan sebagai %s"
    }
    "no_content" {
        en = "No content"
        id = "Tidak ada konten"
    }
    "_about" {
        en = "Efficient paper size calculator. See homepage for more information."
        id = "Kalkulator ukuran plano efisien. Lihat beranda untuk informasi lebih."
    }
    "please_restart" {
        en = "Please restart"
        id = "Mohon mulai ulang"
    }
    "_please_restart" {
        en = "Restart app to see effect."
        id = "Mulai ulang aplikasi untuk melihat perubahan."
    }
    "btn_undo" {
        en = "UNDO"
        id = "KEMBALIKAN"
    }
    "btn_download" {
        en = "DOWNLOAD"
        id = "UNDUH"
    }
    "btn_show_directory" {
        en = "SHOW DIRECTORY"
        id = "TUNJUKKAN FOLDER"
    }
    "btn_homepage" {
        en = "HOMEPAGE"
        id = "BERANDA"
    }
    "btn_close" {
        en = "CLOSE"
        id = "TUTUP"
    }

    // android-only
    "_incomplete" {
        en = "Media and trim size need to be filled."
        id = "Kotak media dan potong perlu diisi."
    }
}