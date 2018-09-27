package ru.handh.hhlocation.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import ru.handh.hhlocation.data.model.BeaconPhysical
import ru.handh.hhlocation.data.model.BeaconShadow

@Dao
interface BeaconPhysicalDao {

    companion object {
        const val TABLE_NAME = "beacon_physical_table"
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(beaconPhysical: BeaconPhysical)

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll()

    @Query("SELECT * FROM $TABLE_NAME ORDER BY distance ASC")
    fun getAllBeacons(): Flowable<List<BeaconPhysical>>
}