package com.hendraanggrian.plano.data

import com.hendraanggrian.plano.Size
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object RecentMediaSizes : IntIdTable() {
    val width = double("width")
    val height = double("height")
}

class RecentMediaSize(id: EntityID<Int>) : IntEntity(id), Size {
    override var width by RecentMediaSizes.width
    override var height by RecentMediaSizes.height

    companion object : IntEntityClass<RecentMediaSize>(RecentMediaSizes) {
        fun contains(width: Double, height: Double): Boolean =
            !find { RecentMediaSizes.width.eq(width) and RecentMediaSizes.height.eq(height) }
                .empty()

        fun limitSize() = all().reversed().drop(10).forEach { RecentMediaSize[it.id].delete() }
    }
}

object RecentTrimSizes : IntIdTable() {
    val width = double("width")
    val height = double("height")
}

class RecentTrimSize(id: EntityID<Int>) : IntEntity(id), Size {
    override var width by RecentTrimSizes.width
    override var height by RecentTrimSizes.height

    companion object : IntEntityClass<RecentTrimSize>(RecentTrimSizes) {
        fun contains(width: Double, height: Double): Boolean =
            !find { RecentTrimSizes.width.eq(width) and RecentTrimSizes.height.eq(height) }
                .empty()

        fun limitSize() = all().reversed().drop(10).forEach { RecentTrimSize[it.id].delete() }
    }
}

fun saveRecentSizes(mediaWidth: Double, mediaHeight: Double, trimWidth: Double, trimHeight: Double) {
    transaction {
        if (!RecentMediaSize.contains(mediaWidth, mediaHeight)) {
            RecentMediaSize.new { width = mediaWidth; height = mediaHeight }
            RecentMediaSize.limitSize()
        }
        if (!RecentTrimSize.contains(trimWidth, trimHeight)) {
            RecentTrimSize.new { width = trimWidth; height = trimHeight }
            RecentTrimSize.limitSize()
        }
    }
}
