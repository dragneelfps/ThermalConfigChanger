package com.nooblabs.thermalconfigchanger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentValue = ShellHelper.getCurrent()
        val currentSelection = ThermalMode.values().firstOrNull { it.value == currentValue }
        if(currentSelection == null) {

        } else {
            init(currentSelection)
        }
    }

    private fun init(currentSelection: ThermalMode) {
        tv_selected.text = getString(R.string.selected, currentSelection.name)
        val adapter = ArrayAdapter<ThermalMode>(this, android.R.layout.simple_spinner_dropdown_item,
            ThermalMode.values())
        thermal_modes.adapter = adapter
        thermal_modes.setSelection(ThermalMode.values().indexOf(currentSelection))

        thermal_modes.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("DRAG", "onNothingSelected")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent ?: return
                val item = parent.getItemAtPosition(position) as ThermalMode
                ShellHelper.updateSConfig(item.value)
                tv_selected.text = getString(R.string.selected, item.name)
                Toast.makeText(this@MainActivity, "Selected $item", Toast.LENGTH_LONG).show()
            }
        }
    }
}
