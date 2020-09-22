package com.hendraanggrian.plano.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.hendraanggrian.plano.Size

@Entity(
    tableName = "trim_boxes",
    foreignKeys = [ForeignKey(entity = MediaBox::class, parentColumns = ["id"], childColumns = ["media_box_id"])]
)
data class TrimBox(
    val x: Float,
    val y: Float,
    override val width: Float,
    override val height: Float,
    @ColumnInfo(name = "media_box_id") val mediaBoxId: Long
) : Size {

    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
