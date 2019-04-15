import java.util.Locale

group = RELEASE_GROUP
version = RELEASE_VERSION

plugins {
    `java-library`
    kotlin("jvm")
    id("com.hendraanggrian.locale")
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

    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(truth())

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
        main = "com.github.shyiko.ktlint.Main"
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
        main = "com.github.shyiko.ktlint.Main"
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
    "clear" {
        en = "Clear"
        id = "Hapus semua"
    }
    "_boxes_cleared" {
        en = "Boxes cleared."
        id = "Konten terhapus."
    }
    "toggle_scale" {
        en = "Toggle scale"
        id = "Alihkan skala"
    }
    "settings" {
        en = "Settings"
        id = "Setelan"
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
    "trim_box" {
        en = "Trim box"
        id = "Kotak potong"
    }
    "bleed" {
        en = "Bleed"
        id = "Lebihan"
    }
    "a_series" {
        en = "A Series"
        id = "Seri A"
    }
    "b_series" {
        en = "B Series"
        id = "Seri B"
    }
    "save" {
        en = "Save"
        id = "Simpan"
    }
    "delete" {
        en = "Delete"
        id = "Hapus"
    }
    "_save_desc" {
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
    "_please_restart_desc" {
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
    "btn_open" {
        en = "OPEN"
        id = "BUKA"
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