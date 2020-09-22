package com.hendraanggrian.plano.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hendraanggrian.plano.Size

@Entity(tableName = "media_boxes")
data class MediaBox(
    override val width: Float,
    override val height: Float
) : Size {

    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
