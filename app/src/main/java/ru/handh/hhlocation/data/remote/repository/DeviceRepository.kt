package ru.handh.hhlocation.data.remote.repository

import io.reactivex.Single
import ru.handh.hhlocation.data.model.RegisterDeviceRequest
import ru.handh.hhlocation.data.model.RegisterDeviceResponse
import ru.handh.hhlocation.data.remote.api.ApiService

class DeviceRepository(val apiService: ApiService) {

    fun registerDevice(uuid: String, name: String): Single<RegisterDeviceResponse> {
        return apiService.registerDevice("fsdf", RegisterDeviceRequest(uuid, name))
    }
}