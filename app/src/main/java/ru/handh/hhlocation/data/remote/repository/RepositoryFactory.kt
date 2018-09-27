package ru.handh.hhlocation.data.remote.repository

import ru.handh.hhlocation.data.remote.api.ApiServiceFactory

object RepositoryFactory {

    val apiService = ApiServiceFactory.makeApiClient()

    fun makeBeaconRepository(): BeaconRepository {
        return BeaconRepository(apiService)
    }

    fun makeDeviceRepository(): DeviceRepository {
        return DeviceRepository(apiService)
    }

    fun positionRepository(): PositionRepository {
        return PositionRepository(apiService)
    }
}