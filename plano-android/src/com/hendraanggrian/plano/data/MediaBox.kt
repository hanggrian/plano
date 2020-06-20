package com.hendraanggrian.plano.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_boxes")
data class MediaBox(
    val width: Double,
    val height: Double
) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
