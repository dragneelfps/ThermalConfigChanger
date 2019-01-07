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

    private val forceSetServiceSetChanged = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            log("force_set_state_changed")
            invalidateOptionsMenu()
        }
    }

    private val forceSetReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            log("received signal from force_set service.")
//            Toast.makeText(this@MainActivity, "received signal from force_set service.", Toast.LENGTH_SHORT)
//                    .show()
            init()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //sets up notification channel if not set up already
        createNotificationChannel()

        adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            ThermalMode.values()
        )
        thermal_modes.adapter = adapter

        btn_xda.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data =
                        Uri.parse("https://forum.xda-developers.com/poco-f1/themes/thermal-config-changer-pocophoneroot-t3886603")
            })
        }
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
        init()
        registerReceiver(forceSetReceiver, IntentFilter("force_set"))
        registerReceiver(forceSetServiceSetChanged, IntentFilter("force_set_state_changed"))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(forceSetReceiver)
        unregisterReceiver(forceSetServiceSetChanged)
    }

    private fun init() {
        val currentSelection = currentActualMode
        if (currentSelection == null) {
            tv_selected.text = "INVALID FILE CONTENTS"
        } else {
            tv_selected.text = getString(R.string.selected, currentSelection.name)
        }
        thermal_modes.onItemSelectedListener = null
        thermal_modes.setSelection(adapter.getPosition(currentSelection), false)
        thermal_modes.onItemSelectedListener = onItemSelectedListener
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_options, menu)
        menu?.findItem(R.id.fore_set)?.isChecked =
                getSharedPreference(IS_FORCE_SET_ENABLE, false) as Boolean
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.fore_set -> {
                item.isChecked = !item.isChecked
                if (item.isChecked) {
                    startForceSetService()
                } else {
                    stopForceSetService()
                }
                true
            }
            R.id.profile_chooser -> {
                Intent(this, ProfileChooserActivity::class.java).apply {
                    startActivity(this)
                }
                true
            }
            R.id.open_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
