package ru.handh.hhlocation.data.local

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import ru.handh.hhlocation.data.model.Beacon

@Dao
interface BeaconDao {

    companion object {
        const val TABLE_NAME = "beacon_table"
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(beacon: Beacon)

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll()

    @Query("SELECT * FROM $TABLE_NAME ORDER BY id ASC")
    fun getAllBeacons(): List<Beacon>
}