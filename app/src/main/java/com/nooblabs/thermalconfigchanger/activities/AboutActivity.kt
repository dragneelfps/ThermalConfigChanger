package com.nooblabs.thermalconfigchanger.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nooblabs.thermalconfigchanger.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
