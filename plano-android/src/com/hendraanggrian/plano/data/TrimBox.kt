package com.hendraanggrian.plano.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "trim_boxes",
    foreignKeys = [ForeignKey(entity = MediaBox::class, parentColumns = ["id"], childColumns = ["media_box_id"])]
)
data class TrimBox(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
    @ColumnInfo(name = "media_box_id") val mediaBoxId: Long
) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
