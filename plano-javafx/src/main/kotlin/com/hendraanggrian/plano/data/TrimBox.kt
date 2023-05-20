package com.hendraanggrian.plano.data

import com.hendraanggrian.plano.Size
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TrimBoxes : IntIdTable() {
    val x = float("x")
    val y = float("y")
    val width = float("width")
    val height = float("height")
    val mediaBox = reference("media_box", MediaBoxes)
}

class TrimBox(id: EntityID<Int>) : IntEntity(id), Size {
    companion object : IntEntityClass<TrimBox>(TrimBoxes)

    var x by TrimBoxes.x
    var y by TrimBoxes.y
    override var width by TrimBoxes.width
    override var height by TrimBoxes.height
    var mediaBox by MediaBox referencedOn TrimBoxes.mediaBox
}
