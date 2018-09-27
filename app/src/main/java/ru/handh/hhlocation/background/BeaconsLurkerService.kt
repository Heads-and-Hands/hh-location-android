package ru.handh.hhlocation.background

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.app.NotificationCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import ru.handh.hhlocation.R
import ru.handh.hhlocation.data.local.BeaconPhysicalDao
import ru.handh.hhlocation.data.local.BeaconShadowDao
import ru.handh.hhlocation.data.local.DatabaseHelper
import ru.handh.hhlocation.data.model.BeaconPhysical
import ru.handh.hhlocation.data.model.BeaconShadow
import ru.handh.hhlocation.data.remote.repository.PositionRepository
import ru.handh.hhlocation.data.remote.repository.RepositoryFactory
import ru.handh.hhlocation.ui.common.DeviceUuidFactory
import ru.handh.hhlocation.ui.home.HomeActivity


class BeaconsLurkerService : Service(), BeaconConsumer {

    private lateinit var beaconManager: BeaconManager
    private var beaconShadowDao: BeaconShadowDao? = null
    private var beaconPhysicalDao: BeaconPhysicalDao? = null

    private val physicalBeacons = mutableMapOf<String, BeaconPhysical>()
    private var shadowBeacons = emptyList<BeaconShadow>()
    private var nearestBeacon: BeaconPhysical? = null

    private var positionRepository: PositionRepository? = null
    private var checkInDisposable: Disposable? = null

    private var getAllShadowBeaconsFlowable: Disposable? = null

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "CHANNEL_ID_FOREGROUND"

        const val REGION_ID = "ru.handh.hhlocation.android"
        const val BEACON_UUID = "fda50693-a4e2-4fb1-afcf-c6eb07647825"

        fun createStartIntent(context: Context): Intent {
            return Intent(context, BeaconsLurkerService::class.java)
        }
    }

    override fun onCreate() {
        super.onCreate()

        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"))
        beaconManager.setEnableScheduledScanJobs(false)
        beaconManager.backgroundBetweenScanPeriod = 0
        beaconManager.backgroundScanPeriod = 6000

        beaconShadowDao = DatabaseHelper.getDatabase(this)?.beaconShadowDao()
        beaconPhysicalDao = DatabaseHelper.getDatabase(this)?.beaconPhysicalDao()

        positionRepository = RepositoryFactory.positionRepository()

        getAllShadowBeaconsFlowable?.dispose()
        if (beaconShadowDao != null) {
            getAllShadowBeaconsFlowable = beaconShadowDao!!.getAllBeacons()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        shadowBeacons = it
                    }, {

                    })
        }

        startForeground(NOTIFICATION_ID, createForegroundNotification())

    }

    override fun onDestroy() {
        BeaconsUpdateJobService.cancel(this)
        beaconManager.unbind(this)
        getAllShadowBeaconsFlowable?.dispose()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // At first, add update beacons list task to the schedule
        BeaconsUpdateJobService.schedule(this)

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        }
        beaconManager.bind(this)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onBeaconServiceConnect() {
        beaconManager.addRangeNotifier { beacons, region ->
            beacons.forEach { beacon ->
                val beaconPhysical = BeaconPhysical(
                        beacon.bluetoothAddress,
                        beacon.id1.toHexString(),
                        beacon.id2.toInt().toString(),
                        beacon.id3.toInt().toString(),
                        beacon.rssi,
                        beacon.txPower,
                        System.currentTimeMillis(),
                        beacon.distance
                )
                // beaconPhysicalDao?.insert(beaconPhysical)
                physicalBeacons[beaconPhysical.macAddress] = beaconPhysical

                var newNearestBeacon: BeaconPhysical? = null
                physicalBeacons.forEach {entry ->
                    if (newNearestBeacon?.distance ?: Double.POSITIVE_INFINITY > entry.value.distance) {
                        newNearestBeacon = entry.value
                    }
                }
                if (nearestBeacon?.macAddress != newNearestBeacon?.macAddress) {
                    checkInDisposable?.dispose()

                    val found = shadowBeacons.firstOrNull {
                        newNearestBeacon?.minor?.toInt() == it.uid
                    }

                    val positionX = found?.posX ?: 0
                    val positionY = found?.posY ?: 0

                    if (positionRepository != null) {
                        checkInDisposable?.dispose()
                        checkInDisposable = positionRepository!!.checkIn(getLocalDeviceId(), positionX, positionY)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({

                                }, {

                                })
                    }

                    nearestBeacon = newNearestBeacon
                }
            }
        }

        try {
            beaconManager.startRangingBeaconsInRegion(Region(REGION_ID, null, null, null))
        } catch (ex: RemoteException) {

        }
    }

    private fun createForegroundNotification(): Notification {
        val intent = HomeActivity.createStartIntent(this)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        prepareChannel()
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setContentIntent(PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT))
                .build()
    }

    private fun prepareChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val appName = getString(R.string.app_name)
            val channelDescription = NOTIFICATION_CHANNEL_ID
            val notificationManager = getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager?

            if (notificationManager != null) {
                var channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)

                if (channel == null) {
                    channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, appName, NotificationManager.IMPORTANCE_LOW)
                    channel.description = channelDescription
                    notificationManager.createNotificationChannel(channel)
                }
            }

        }
    }

    private fun getLocalDeviceId(): String {
        return DeviceUuidFactory(this).deviceUuid?.toString() ?: ""
    }
}