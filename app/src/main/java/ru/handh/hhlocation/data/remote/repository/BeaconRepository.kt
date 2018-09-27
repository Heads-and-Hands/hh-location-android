package ru.handh.hhlocation.data.remote.repository

import io.reactivex.Single
import ru.handh.hhlocation.data.model.BeaconShadow
import ru.handh.hhlocation.data.remote.api.ApiService

class BeaconRepository(val apiService: ApiService) {

    fun getBeacons(): Single<List<BeaconShadow>> {
        return apiService.getBeacons("fsdf")
    }
}