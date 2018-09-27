package ru.handh.hhlocation.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.handh.hhlocation.data.local.BeaconShadowDao

@Entity(tableName = BeaconShadowDao.TABLE_NAME)
data class BeaconShadow(
        @PrimaryKey val id: Int,
        val uid: Int,
        val name: String,
        val correction: Int,
        @SerializedName("posX") val posX: Int,
        @SerializedName("posY") val posY: Int
)