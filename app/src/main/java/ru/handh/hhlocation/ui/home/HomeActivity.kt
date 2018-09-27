package ru.handh.hhlocation.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import ru.handh.hhlocation.R
import ru.handh.hhlocation.background.BeaconsLurkerService
import ru.handh.hhlocation.data.local.PreferenceHelper
import ru.handh.hhlocation.data.remote.repository.RepositoryFactory
import ru.handh.hhlocation.ext.showToast
import ru.handh.hhlocation.ext.startForegroundServiceCompat
import ru.handh.hhlocation.ui.common.DeviceUuidFactory
import ru.handh.hhlocation.ui.monitor.MonitorActivity


class HomeActivity: AppCompatActivity() {

    private var registerDeviceDisposable: Disposable? = null

    private var localDeviceId = ""
    private var localBluetoothName = ""

    companion object {
        const val PERMISSION_REQUEST_COARSE_LOCATION = 1

        fun createStartIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupLayout()
    }

    override fun onDestroy() {
        registerDeviceDisposable?.dispose()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if ((requestCode == PERMISSION_REQUEST_COARSE_LOCATION) and (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            registerDeviceOnBackend()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun onRegisterDeviceClick() {
        checkCoarseLocationPermission()
    }

    @SuppressLint("HardwareIds")
    private fun getLocalBluetoothName(): String {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var name = bluetoothAdapter?.name
        if (name == null) {
            name = bluetoothAdapter?.address
        }
        return name ?: "Unknown"
    }

    private fun getLocalDeviceId(): String {
        return DeviceUuidFactory(this).deviceUuid?.toString() ?: ""
    }

    private fun renderButtonText(alreadyRegistered: Boolean) {
        buttonRegisterDevice.setText(if (alreadyRegistered) R.string.re_register_device else R.string.register_device)
    }

    private fun startBeaconsLurker() {
        startForegroundServiceCompat(BeaconsLurkerService.createStartIntent(this))
    }

    private fun checkCoarseLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_COARSE_LOCATION)
            } else {
                registerDeviceOnBackend()
            }
        } else {
            registerDeviceOnBackend()
        }
    }

    private fun registerDeviceOnBackend() {
        registerDeviceDisposable?.dispose()
        registerDeviceDisposable = RepositoryFactory.makeDeviceRepository().registerDevice(localDeviceId, localBluetoothName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    showToast(R.string.device_successfully_registered)

                    val alreadyRegistered = it.isOk()
                    PreferenceHelper(this).alreadyRegistered = alreadyRegistered
                    renderButtonText(alreadyRegistered)

                    startBeaconsLurker()
                }, {
                    showToast(it.localizedMessage)
                })
    }

    private fun setupLayout() {
        localDeviceId = getLocalDeviceId()
        localBluetoothName = getLocalBluetoothName()

        textViewDeviceId.text = localDeviceId
        textViewDeviceTitle.text = localBluetoothName

        buttonRegisterDevice.setOnClickListener {
            onRegisterDeviceClick()
        }

        renderButtonText(PreferenceHelper(this).alreadyRegistered)

        toolbar.inflateMenu(R.menu.home_menu)
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.menuMonitor) {
                startActivity(MonitorActivity.createStartIntent(this))
            }
            return@setOnMenuItemClickListener false
        }
    }
}