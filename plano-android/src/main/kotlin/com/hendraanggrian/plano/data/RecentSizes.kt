package com.hendraanggrian.plano.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.hendraanggrian.plano.Size

@Entity(tableName = "recent_media_sizes")
data class RecentMediaSize(
    override val width: Float,
    override val height: Float
) : Size {

    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

@Dao
interface RecentMediaSizes {

    @Query("SELECT * FROM recent_media_sizes")
    suspend fun all(): List<RecentMediaSize>

    @Insert
    suspend fun insertAll(vararg sizes: RecentMediaSize)

    @Delete
    suspend fun delete(user: RecentMediaSize)

    @Query("DELETE FROM recent_media_sizes")
    suspend fun deleteAll()

    suspend fun contains(width: Float, height: Float): Boolean =
        all().any { it.width == width && it.height == height }

    suspend fun limitSize() = all().reversed().drop(5).forEach { delete(it) }
}

@Entity(tableName = "recent_trim_sizes")
data class RecentTrimSize(
    override val width: Float,
    override val height: Float
) : Size {

    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

@Dao
interface RecentTrimSizes {

    @Query("SELECT * FROM recent_trim_sizes")
    suspend fun all(): List<RecentTrimSize>

    @Insert
    suspend fun insertAll(vararg sizes: RecentTrimSize)

    @Delete
    suspend fun delete(user: RecentTrimSize)

    @Query("DELETE FROM recent_trim_sizes")
    suspend fun deleteAll()

    suspend fun contains(width: Float, height: Float): Boolean =
        all().any { it.width == width && it.height == height }

    suspend fun limitSize() = all().reversed().drop(5).forEach { delete(it) }
}

suspend fun Context.saveRecentSizes(mediaWidth: Float, mediaHeight: Float, trimWidth: Float, trimHeight: Float) {
    val db = PlanoDatabase.getInstance(this@saveRecentSizes)
    if (!db.recentMedia().contains(mediaWidth, mediaHeight)) {
        db.recentMedia().insertAll(RecentMediaSize(mediaWidth, mediaHeight))
        db.recentMedia().limitSize()
    }
    if (!db.recentTrim().contains(trimWidth, trimHeight)) {
        db.recentTrim().insertAll(RecentTrimSize(trimWidth, trimHeight))
        db.recentTrim().limitSize()
    }
}
