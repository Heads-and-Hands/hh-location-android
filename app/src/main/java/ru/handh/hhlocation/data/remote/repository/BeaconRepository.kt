package ru.handh.hhlocation.data.remote.repository

import io.reactivex.Single
import ru.handh.hhlocation.data.model.Beacon
import ru.handh.hhlocation.data.remote.api.ApiService

class BeaconRepository(val apiService: ApiService) {

    fun getBeacons(): Single<List<Beacon>> {
        return apiService.getBeacons("fsdf")
    }
}