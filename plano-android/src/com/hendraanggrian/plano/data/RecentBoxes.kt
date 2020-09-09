package com.hendraanggrian.plano.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.hendraanggrian.plano.Box

@Entity(tableName = "recent_media_boxes")
data class RecentMediaBox(
    override val width: Double,
    override val height: Double
) : Box {

    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

@Dao
interface RecentMediaBoxes {
    @Query("SELECT * FROM recent_media_boxes") suspend fun all(): List<RecentMediaBox>
    @Insert suspend fun insertAll(vararg boxes: RecentMediaBox)
    @Delete suspend fun delete(user: RecentMediaBox)

    suspend fun contains(width: Double, height: Double): Boolean =
        all().any { it.width == width && it.height == height }

    suspend fun limitSize() = all().reversed().drop(5).forEach { delete(it) }
}

@Entity(tableName = "recent_trim_boxes")
data class RecentTrimBox(
    override val width: Double,
    override val height: Double
) : Box {

    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

@Dao
interface RecentTrimBoxes {
    @Query("SELECT * FROM recent_trim_boxes") suspend fun all(): List<RecentTrimBox>
    @Insert suspend fun insertAll(vararg boxes: RecentTrimBox)
    @Delete suspend fun delete(user: RecentTrimBox)

    suspend fun contains(width: Double, height: Double): Boolean =
        all().any { it.width == width && it.height == height }

    suspend fun limitSize() = all().reversed().drop(5).forEach { delete(it) }
}

suspend fun Context.saveRecentBoxes(mediaWidth: Double, mediaHeight: Double, trimWidth: Double, trimHeight: Double) {
    val db = PlanoDatabase.getInstance(this@saveRecentBoxes)
    if (!db.historyMediaBox().contains(mediaWidth, mediaHeight)) {
        db.historyMediaBox().insertAll(RecentMediaBox(mediaWidth, mediaHeight))
        db.historyMediaBox().limitSize()
    }
    if (!db.historyTrimBox().contains(trimWidth, trimHeight)) {
        db.historyTrimBox().insertAll(RecentTrimBox(trimWidth, trimHeight))
        db.historyTrimBox().limitSize()
    }
}
