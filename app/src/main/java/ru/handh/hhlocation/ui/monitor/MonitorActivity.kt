package ru.handh.hhlocation.ui.monitor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_monitor.*
import ru.handh.hhlocation.R
import ru.handh.hhlocation.ui.home.HomeActivity

class MonitorActivity : AppCompatActivity() {

    companion object {
        fun createStartIntent(context: Context): Intent {
            return Intent(context, MonitorActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)

        setupLayout()
    }

    private fun setupLayout() {
        toolbar.inflateMenu(R.menu.monitor_menu)
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.menuHome) {
                startActivity(HomeActivity.createStartIntent(this))
            }
            return@setOnMenuItemClickListener false
        }
    }
}