package com.hendraanggrian.plano.data

import com.hendraanggrian.plano.Box
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TrimBoxes : IntIdTable() {
    val x = double("x")
    val y = double("y")
    val width = double("width")
    val height = double("height")
    val mediaBox = reference("media_box", MediaBoxes)
}

class TrimBox(id: EntityID<Int>) : IntEntity(id), Box {
    companion object : IntEntityClass<TrimBox>(TrimBoxes)

    var x by TrimBoxes.x
    var y by TrimBoxes.y
    override var width by TrimBoxes.width
    override var height by TrimBoxes.height
    var mediaBox by MediaBox referencedOn TrimBoxes.mediaBox
}
