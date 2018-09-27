package ru.handh.hhlocation.ui.start

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.handh.hhlocation.ui.home.HomeActivity

class StartActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(HomeActivity.createStartIntent(this))
    }
}
