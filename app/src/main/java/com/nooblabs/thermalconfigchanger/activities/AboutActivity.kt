package com.nooblabs.thermalconfigchanger.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import com.nooblabs.thermalconfigchanger.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        about_txt.movementMethod = LinkMovementMethod.getInstance()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
