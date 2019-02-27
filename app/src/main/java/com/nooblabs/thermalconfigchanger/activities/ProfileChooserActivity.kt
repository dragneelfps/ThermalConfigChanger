package com.nooblabs.thermalconfigchanger.activities

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.nooblabs.thermalconfigchanger.R
import com.nooblabs.thermalconfigchanger.ThermalMode
import com.nooblabs.thermalconfigchanger.adapters.ApplicationAdapter
import com.nooblabs.thermalconfigchanger.extensions.*
import kotlinx.android.synthetic.main.activity_profile_chooser.*

class ProfileChooserActivity : AppCompatActivity() {

    private lateinit var installedApplications: List<PackageInfo>

    private lateinit var applicationAdapter: ApplicationAdapter

    private lateinit var nm: NotificationManager

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            invalidateOptionsMenu()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_chooser)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        applicationAdapter = ApplicationAdapter(this)
        applicationAdapter.modeChangeListener = object : ApplicationAdapter.OnModeChangeListener {
            override fun onModeChange(packageName: String, newMode: ThermalMode) {
                putSharedPreference(packageName, newMode.value)
            }
        }

        rv_app_list.layoutManager = LinearLayoutManager(this)
        rv_app_list.adapter = applicationAdapter
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
        init()
        registerReceiver(broadcastReceiver, IntentFilter("profiler_state_changed"))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_chooser_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
        when (item?.itemId) {
            R.id.open_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    private fun init() {
        if (checkForUsagePermission()) {
            btn_grant_usage_permission.hide()
            showAll(rv_app_list, app_filter, show_system_apps)

            show_system_apps.setOnCheckedChangeListener { _, isChecked ->
                initApps(isChecked)
                app_filter.text = null
            }
            app_filter.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val searchTerm = s?.toString() ?: ""
                    applicationAdapter.filter(searchTerm)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            })

            initApps(show_system_apps.isChecked)
        } else {
            //ask for user permission
            btn_grant_usage_permission.visibility = View.VISIBLE
            hideAll(rv_app_list, app_filter, show_system_apps)
            btn_grant_usage_permission.setOnClickListener {
                openUsageSettings()
            }
        }

    }

    private fun initApps(showSystemApps: Boolean) {
        progress_bar.show()
        installedApplications = getInstalledApplications(showSystemApps)

        val list = ArrayList<Pair<PackageInfo, ThermalMode>>()

        installedApplications.forEach {
            val mode = getSharedPreference(it.packageName, ThermalMode.DEFAULT.value) as Int
            list.add(Pair(it, ThermalMode.values().first { mode == it.value }))
            log(list[list.size - 1].toString())
        }

        progress_bar.hide()
        applicationAdapter.initializeData(list)
    }
}
