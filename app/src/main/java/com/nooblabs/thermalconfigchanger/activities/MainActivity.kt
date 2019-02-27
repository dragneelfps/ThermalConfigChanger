package com.nooblabs.thermalconfigchanger.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.nooblabs.thermalconfigchanger.R
import com.nooblabs.thermalconfigchanger.ThermalMode
import com.nooblabs.thermalconfigchanger.extensions.*
import com.nooblabs.thermalconfigchanger.services.MainService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<ThermalMode>

    private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            Log.d("DRAG", "onNothingSelected")
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            parent ?: return
            val item = parent.getItemAtPosition(position) as ThermalMode

            //set both
            currentSetMode = item
            currentActualMode = item

            tv_selected.text = getString(R.string.selected, item.name)
            Toast.makeText(this@MainActivity, "Selected $item", Toast.LENGTH_LONG).show()
        }
    }

    private val forceSetReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            log("received signal from force_set service.")
            intent ?: return
            tv_selected.text = getString(R.string.selected, intent.getStringExtra(FORCE_SET_PAYLOAD))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            ThermalMode.values()
        )
        thermal_modes.adapter = adapter

        btn_xda.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(XDA_LINK)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
        init()
        registerReceiver(forceSetReceiver, IntentFilter(FORCE_SET_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(forceSetReceiver)
    }

    private fun init() {
        val currentSelection = currentActualMode
        if (currentSelection == null) {
            tv_selected.text = getString(R.string.invalid_file_contents_error)
        } else {
            tv_selected.text = getString(R.string.selected, currentSelection.name)
        }
        thermal_modes.onItemSelectedListener = null
        thermal_modes.setSelection(adapter.getPosition(currentSelection), false)
        thermal_modes.onItemSelectedListener = onItemSelectedListener

        val isServiceEnabled = getSharedPreference(IS_SERVICE_ENABLED, false) as Boolean

        enable_service.isChecked = isServiceEnabled
        toggleControls(isServiceEnabled)

        enable_service.setOnCheckedChangeListener { btn, isChecked ->

            btn.setText(if(isChecked) R.string.service_toggle_off_label else R.string.service_toggle_on_label)
            putSharedPreference(IS_SERVICE_ENABLED, isChecked)

            toggleControls(isChecked)

            if(isChecked) {
                MainService.startService(this)
            }

        }


        force_set_switch.isChecked = getSharedPreference(IS_FORCE_SET_ENABLE, false) as Boolean
        force_set_switch.setOnCheckedChangeListener { _, isChecked ->
            putSharedPreference(IS_FORCE_SET_ENABLE, isChecked)
        }


        show_mode_switch.isChecked = getSharedPreference(SHOW_MODE_NOTIFICATION, false) as Boolean
        show_mode_switch.setOnCheckedChangeListener { _, isChecked ->
            putSharedPreference(SHOW_MODE_NOTIFICATION, isChecked)
        }

        show_toast_switch.isChecked = getSharedPreference(IS_TOAST_ENABLED, false) as Boolean
        show_toast_switch.setOnCheckedChangeListener { _, isChecked ->
            putSharedPreference(IS_TOAST_ENABLED, isChecked)
        }

        profiler_switch.isChecked = getSharedPreference(IS_PROFILER_ENABLED, false) as Boolean
        profiler_switch.setOnCheckedChangeListener { _, isChecked ->
            putSharedPreference(IS_PROFILER_ENABLED, isChecked)
            open_profiler.isEnabled = isChecked
        }

        open_profiler.setOnClickListener {
            startActivity(Intent(this, ProfileChooserActivity::class.java))
        }

        apply_on_boot_switch.isChecked = getSharedPreference(APPLY_ON_BOOT_ENABLED, false) as Boolean
        apply_on_boot_switch.setOnCheckedChangeListener { _, isChecked ->
            putSharedPreference(APPLY_ON_BOOT_ENABLED, isChecked)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.open_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleControls(isEnabled: Boolean) {
        if(isEnabled) {
            showAll(force_set_switch, show_mode_switch, show_toast_switch, profiler_switch,
                    open_profiler, apply_on_boot_switch)
        } else {
            hideAll(force_set_switch, show_mode_switch, show_toast_switch, profiler_switch,
                    open_profiler, apply_on_boot_switch)
        }
    }
}
