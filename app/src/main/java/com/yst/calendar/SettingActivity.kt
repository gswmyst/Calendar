package com.yst.calendar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        button.isEnabled=false
        MyName.addTextChangedListener(object :TextWatcher{//edittext textchanged消息监听器
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable){
                button.isEnabled = !(MyName.length()==0||LoversName.length()==0)//禁用/启用按钮
            }
        })
        LoversName.addTextChangedListener(object :TextWatcher{//edittext textchanged消息监听器
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable){
                button.isEnabled = !(MyName.length()==0||LoversName.length()==0)//禁用/启用按钮
            }
        })
        button.setOnClickListener{//按钮消息监听器
            val sp: SharedPreferences =this.getSharedPreferences("preference", Context.MODE_PRIVATE)//sharedpreference对象
            val editor:SharedPreferences.Editor=sp.edit()
            editor.putString("myname", MyName.text.toString())//添加双方称呼
            editor.putString("lovername",LoversName.text.toString())
            var month=(datePicker.month+1).toString()//获取设置的日期，若月份和天数小于10则前面加上0
            if (month.length==1)
                month="0"+month
            var day=datePicker.dayOfMonth.toString()
            if (day.length==1)
                day="0"+day
            val str=datePicker.year.toString()+"-"+month+"-"+day//设置为iso标准日期格式
            editor.putString("date",str)//添加日期
            if (editor.commit()) {//提交后打开MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()//关闭这个activity
            }
        }
    }
}