package com.yst.calendar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.constraintlayout.widget.ConstraintLayout
import com.nanchen.compresshelper.CompressHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.time.LocalDate
import java.time.Period
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager

import android.app.Activity
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_setting.*
import java.io.FileOutputStream
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private val imagecode=1//选择图片的request code
    private val REQUEST_EXTERNAL_STORAGE=1//获取存储权限参数
    private val PERMISSIONS_STORAGE = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        verifyStoragePermissions(this)
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
        val period=Period.between(startdate,today)//获取period,包含年月日
        val days=today.toEpochDay()-startdate.toEpochDay()+1//计算相差天数
        textview.text="今天是"+strmyname+"和"+strlovename+"在一起的第"+days+"天"//输出
        val path=getExternalFilesDir("background/background.jpg").toString()//图片路径
        if (!TextUtils.isEmpty(path)){//路径不存在则不加载图片
            val bitmap=BitmapFactory.decodeFile(path)
            try {//判断图片是否正确加载
                bitmap.width
            }catch (e:Exception){}
            val drawable=BitmapDrawable(resources,bitmap)//设置背景图片
            cl.background=drawable
            cl.background.alpha=50
        }
        switch1.setOnCheckedChangeListener{switch1,isChecked->//切换天数格式
            if (isChecked){
                switch1.text=getString(R.string.yy_mm_dd)
                val days=period.days+1
                if (period.years==0 && period.months==0)
                    textview.text="今天是"+strmyname+"和"+strlovename+"在一起的第"+days+"天"
                else if (period.years==0)
                    textview.text="今天是"+strmyname+"和"+strlovename+"在一起的第"+period.months+"月"+days+"天"
                else if (period.months==0)
                    textview.text="今天是"+strmyname+"和"+strlovename+"在一起的第"+period.years+"年"+days+"天"
                else
                    textview.text="今天是"+strmyname+"和"+strlovename+"在一起的第"+period.years+"年"+period.months+"月"+days+"天"
            }
            else{
                switch1.text=getString(R.string.days)
                textview.text="今天是"+strmyname+"和"+strlovename+"在一起的第"+days+"天"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {//添加菜单
        menu?.add(Menu.NONE,0,0,"重新设置")
        menu?.add(Menu.NONE,1,1,"选择背景图片")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//点击重新设置菜单
        when (item.itemId){//判断打开的是哪个菜单项
            0 -> startActivity(Intent(this,SettingActivity::class.java))//打开设置activity
            1->{
                var getAlbum = Intent(Intent.ACTION_PICK)
                getAlbum.type = "image/*"
                startActivityForResult(getAlbum,imagecode)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//获取得到的图片路径并保存
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode!= RESULT_OK)
            return
        if (requestCode==imagecode){
            val originalURI = data!!.data!!
            val filePathColumn= arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(originalURI,filePathColumn,null,null,null)
            if (cursor!=null){
                cursor.moveToFirst()
                val columnIndex=cursor.getColumnIndexOrThrow(filePathColumn[0])
                val path=cursor.getString(columnIndex)
                val file = File(path)
                if (!file.exists())
                    return
                val bitmap= CompressHelper.Builder(this)
                    .setMaxWidth(1080F)
                    .setMaxHeight(1920F)
                    .setQuality(80)
                    .setFileName("background.jpg")
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(getExternalFilesDir(null).toString())
                    .build()
                    .compressToBitmap(file)
                val drawable=BitmapDrawable(resources,bitmap)
                cl.background=drawable
                cl.background.alpha=50
                saveBitmap(bitmap)
            }
        }
    }

    private fun saveBitmap(bitmap: Bitmap) {//保存图片到data/com.yst.Calendar/files/background
        val appDir=File(getExternalFilesDir(null),"background")
        if (!appDir.exists())
            appDir.mkdir()
        val fileName="background"+".jpg"
        val file=File(appDir,fileName)
        if (file.exists()){
            file.delete()
            file.createNewFile()
        }
        val fos=FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
        fos.flush()
        fos.close()
    }

    private fun verifyStoragePermissions(activity: Activity?) {//获取存储权限函数
        try {
            //检测是否有写的权限
            val permission = ActivityCompat.checkSelfPermission(activity!!, "android.permission.WRITE_EXTERNAL_STORAGE")
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

