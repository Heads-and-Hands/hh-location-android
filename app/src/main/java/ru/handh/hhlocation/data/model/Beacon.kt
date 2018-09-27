package ru.handh.hhlocation.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import ru.handh.hhlocation.data.local.BeaconDao

@Entity(tableName = BeaconDao.TABLE_NAME)
data class Beacon(
        @PrimaryKey val id: Int,
        val uid: Int,
        val name: String,
        val correction: Int,
        val posX: Int,
        val posY: Int
)