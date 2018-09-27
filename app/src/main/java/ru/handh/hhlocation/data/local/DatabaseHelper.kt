package ru.handh.hhlocation.data.local

import android.arch.persistence.room.Room
import android.content.Context

class DatabaseHelper {

    companion object {
        const val DB_NAME = "navi_db"

        @Volatile
        private var naviDatabase: NaviDatabase? = null

        fun getDatabase(context: Context): NaviDatabase? {
            if (naviDatabase == null) {
                synchronized(NaviDatabase::class.java) {
                    if (naviDatabase == null) {
                        naviDatabase = Room.databaseBuilder(context, NaviDatabase::class.java, DB_NAME)
                                .build()
                    }
                }
            }
            return naviDatabase
        }
    }
}