package ru.handh.hhlocation.background

import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.handh.hhlocation.data.local.DatabaseHelper
import ru.handh.hhlocation.data.remote.repository.RepositoryFactory

class BeaconsUpdateJobService : JobService() {

    var getBeaconsDisposable: Disposable? = null

    companion object {
        private val JOB_ID = 1
        private val ONE_DAY_INTERVAL = 24 * 60 * 60 * 1000L // 1 Day

        fun schedule(context: Context) {
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val componentName = ComponentName(context, BeaconsUpdateJobService::class.java)
            val jobBuilder = JobInfo.Builder(JOB_ID, componentName)
            jobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            jobBuilder.setPeriodic(ONE_DAY_INTERVAL)
            jobScheduler.schedule(jobBuilder.build())
        }

        fun cancel(context: Context) {
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(JOB_ID)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        getBeaconsDisposable?.dispose()
        getBeaconsDisposable = RepositoryFactory.makeBeaconRepository().getBeacons()
                .doOnSuccess { beacons ->
                    val naviDatabase = DatabaseHelper.getDatabase(this)
                    val beaconDao = naviDatabase?.beaconDao()
                    if (beaconDao != null) {
                        beacons.forEach {
                            beaconDao.insert(beacon = it)
                        }
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ beacons ->

                }, {

                })

        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        getBeaconsDisposable?.dispose()
        return false
    }
}