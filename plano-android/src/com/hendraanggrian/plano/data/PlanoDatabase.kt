package com.hendraanggrian.plano.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/** As seen in https://github.com/android/sunflower. */
@Database(entities = [RecentMediaBox::class, RecentTrimBox::class], version = 1, exportSchema = false)
abstract class PlanoDatabase : RoomDatabase() {
    abstract fun historyMediaBox(): RecentMediaBoxes
    abstract fun historyTrimBox(): RecentTrimBoxes

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: PlanoDatabase? = null

        fun getInstance(context: Context): PlanoDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): PlanoDatabase =
            Room.databaseBuilder(context, PlanoDatabase::class.java, "plano-db")
                .build()
    }
}
