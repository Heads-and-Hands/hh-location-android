package ru.handh.hhlocation.data.remote.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.handh.hhlocation.data.model.Beacon
import ru.handh.hhlocation.data.model.CheckInRequest
import ru.handh.hhlocation.data.model.RegisterDeviceRequest
import ru.handh.hhlocation.data.model.RegisterDeviceResponse

interface ApiService {

    @GET("beacon")
    fun getBeacons(@Query("token") token: String): Single<List<Beacon>>

    @POST("device")
    fun registerDevice(@Query("token") token: String, @Body request: RegisterDeviceRequest): Single<RegisterDeviceResponse>

    @POST("position")
    fun checkIn(@Query("token") token: String, @Body request: CheckInRequest): Single<ResponseBody>
}