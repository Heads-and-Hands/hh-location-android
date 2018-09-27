package ru.handh.hhlocation.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import ru.handh.hhlocation.data.local.NaviDatabase.Companion.DATABASE_VERSION
import ru.handh.hhlocation.data.model.BeaconPhysical
import ru.handh.hhlocation.data.model.BeaconShadow

@Database(entities = [BeaconShadow::class, BeaconPhysical::class], version = DATABASE_VERSION)
abstract class NaviDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_VERSION = 1
    }

    abstract fun beaconShadowDao(): BeaconShadowDao
    abstract fun beaconPhysicalDao(): BeaconPhysicalDao
}