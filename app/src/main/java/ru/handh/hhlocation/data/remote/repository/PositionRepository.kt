package ru.handh.hhlocation.data.remote.repository

import io.reactivex.Single
import okhttp3.ResponseBody
import ru.handh.hhlocation.data.model.CheckInRequest
import ru.handh.hhlocation.data.remote.api.ApiService

class PositionRepository(val apiService: ApiService) {

    fun checkIn(uuid: String, positionX: Int, positionY: Int): Single<ResponseBody> {
        return apiService.checkIn("fsdf", CheckInRequest(uuid, positionX, positionY))
    }
}