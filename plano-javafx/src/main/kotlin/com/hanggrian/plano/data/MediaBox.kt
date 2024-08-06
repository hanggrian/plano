package com.hanggrian.plano.data

import com.hanggrian.plano.Size
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object MediaBoxes : IntIdTable() {
    val width = float("width")
    val height = float("height")
}

class MediaBox(id: EntityID<Int>) :
    IntEntity(id),
    Size {
    override var width by MediaBoxes.width
    override var height by MediaBoxes.height

    companion object : IntEntityClass<MediaBox>(MediaBoxes)
}
