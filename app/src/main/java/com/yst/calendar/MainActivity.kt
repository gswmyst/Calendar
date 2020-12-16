package com.yst.calendar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sp:SharedPreferences=this.getSharedPreferences("preference",Context.MODE_PRIVATE)
        if (sp.getString("myname","").equals(""))
        {
            startActivity(Intent(this,SettingActivity::class.java))
        }
    }
}