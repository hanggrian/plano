package com.hendraanggrian.plano.data

import com.hendraanggrian.plano.Box
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object RecentMediaBoxes : IntIdTable() {
    val width = double("width")
    val height = double("height")
}

class RecentMediaBox(id: EntityID<Int>) : IntEntity(id), Box {
    override var width by RecentMediaBoxes.width
    override var height by RecentMediaBoxes.height

    companion object : IntEntityClass<RecentMediaBox>(RecentMediaBoxes) {
        fun contains(width: Double, height: Double): Boolean =
            !find { RecentMediaBoxes.width.eq(width) and RecentMediaBoxes.height.eq(height) }
                .empty()

        fun limitSize() = all().reversed().drop(10).forEach { RecentMediaBox[it.id].delete() }
    }
}

object RecentTrimBoxes : IntIdTable() {
    val width = double("width")
    val height = double("height")
}

class RecentTrimBox(id: EntityID<Int>) : IntEntity(id), Box {
    override var width by RecentTrimBoxes.width
    override var height by RecentTrimBoxes.height

    companion object : IntEntityClass<RecentTrimBox>(RecentTrimBoxes) {
        fun contains(width: Double, height: Double): Boolean =
            !find { RecentTrimBoxes.width.eq(width) and RecentTrimBoxes.height.eq(height) }
                .empty()

        fun limitSize() = all().reversed().drop(10).forEach { RecentTrimBox[it.id].delete() }
    }
}

fun saveRecentBoxes(mediaWidth: Double, mediaHeight: Double, trimWidth: Double, trimHeight: Double) {
    transaction {
        if (!RecentMediaBox.contains(mediaWidth, mediaHeight)) {
            RecentMediaBox.new { width = mediaWidth; height = mediaHeight }
            RecentMediaBox.limitSize()
        }
        if (!RecentTrimBox.contains(trimWidth, trimHeight)) {
            RecentTrimBox.new { width = trimWidth; height = trimHeight }
            RecentTrimBox.limitSize()
        }
    }
}
