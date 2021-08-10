package com.android.upchaar.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.upchaar.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        online_appoinment.setOnClickListener{

        }

        offline_appointment.setOnClickListener{

        }

        appointment.setOnClickListener{

        }

        profile.setOnClickListener{

        }
    }
}