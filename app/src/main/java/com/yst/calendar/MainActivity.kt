package com.yst.calendar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.Period


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sp:SharedPreferences=this.getSharedPreferences("preference",Context.MODE_PRIVATE)//sharedpreference对象
        val strmyname=sp.getString("myname","")//获取sharedpreference中我的称呼
        if (strmyname.equals(""))//如果没有我的称呼则去设置activity
        {
            startActivity(Intent(this,SettingActivity::class.java))//打开设置activity
            finish()//关闭这个activity
        }
        val strlovename=sp.getString("lovername","")//获取对方的称呼
        val startdatestr=sp.getString("date","")//获取开始时间
        val startdate=LocalDate.parse(startdatestr)//开始时间string转化为localdate
        val today=LocalDate.now()//获取当前日期
        //val period=Period.between(startdate,today)//获取period,包含年月日
        val days=today.toEpochDay()-startdate.toEpochDay()+1//计算相差天数
        textview.text="今天是"+strmyname+"和"+strlovename+"在一起的第"+days+"天"//输出
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {//添加菜单
        menu?.add(Menu.NONE,0,0,"重新设置")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//点击重新设置菜单
        val id=item.itemId
        when (id){
            0->{
                startActivity(Intent(this,SettingActivity::class.java))//打开设置activity
            }
        }
        return super.onOptionsItemSelected(item)
    }

}