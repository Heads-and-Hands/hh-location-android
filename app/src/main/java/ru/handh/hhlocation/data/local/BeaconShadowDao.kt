package ru.handh.hhlocation.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import ru.handh.hhlocation.data.model.BeaconShadow

@Dao
interface BeaconShadowDao {

    companion object {
        const val TABLE_NAME = "beacon_table"
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(beaconShadow: BeaconShadow)

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll()

    @Query("SELECT * FROM $TABLE_NAME ORDER BY id ASC")
    fun getAllBeacons(): Flowable<List<BeaconShadow>>
}