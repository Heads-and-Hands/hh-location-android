package ru.handh.hhlocation.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import ru.handh.hhlocation.data.local.BeaconPhysicalDao

@Entity(tableName = BeaconPhysicalDao.TABLE_NAME)
data class BeaconPhysical(
        val macAddress: String,
        val uuid: String,
        val major: String,
        val minor: String,
        val rssi: Int,
        val tx: Int,
        val lastUpdate: Long,
        val distance: Double
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}