package ru.handh.hhlocation.background

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.app.NotificationCompat
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Region
import ru.handh.hhlocation.R
import ru.handh.hhlocation.ui.home.HomeActivity



class BeaconsLurkerService: Service(), BeaconConsumer {

    private lateinit var beaconManager: BeaconManager

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "CHANNEL_ID_FOREGROUND"

        const val REGION_UUID = "FDA50693-A4E2-4FB1-AFCF-C6EB07647825"

        fun createStartIntent(context: Context): Intent {
            return Intent(context, BeaconsLurkerService::class.java)
        }
    }

    override fun onCreate() {
        super.onCreate()

        beaconManager = BeaconManager.getInstanceForApplication(this)

        startForeground(NOTIFICATION_ID, createForegroundNotification())

    }

    override fun onDestroy() {
        BeaconsUpdateJobService.cancel(this)
        beaconManager.unbind(this)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // At first, add update beacons list task to the schedule
        BeaconsUpdateJobService.schedule(this)

        beaconManager.bind(this)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onBeaconServiceConnect() {
        beaconManager.addRangeNotifier { beacons, region ->
            beacons.forEach {
                // ~
            }
        }

        try {
            beaconManager.startRangingBeaconsInRegion(Region(REGION_UUID, null, null, null))
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
}