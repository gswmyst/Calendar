package com.yst.calendar

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.time.LocalDate
import java.time.Period
import android.graphics.BitmapFactory
import android.text.TextUtils
import java.io.FileOutputStream
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private val IMAGE_CODE = 1//选择图片的request code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sp: SharedPreferences = this.getSharedPreferences("preference", MODE_PRIVATE)//sharedpreference对象
        val strMyName = sp.getString("myname", "")//获取sharedpreference中我的称呼
        if (strMyName.equals(""))//如果没有我的称呼则去设置activity
        {
            startActivity(Intent(this, SettingActivity::class.java))//打开设置activity
            finish()//关闭这个activity
        }
        val strLovesName = sp.getString("lovername", "")//获取对方的称呼
        val startDateStr = sp.getString("date", "")//获取开始时间
        val startDate = LocalDate.parse(startDateStr)//开始时间string转化为localdate
        val today = LocalDate.now()//获取当前日期
        val period = Period.between(startDate, today)//获取period,包含年月日
        val days = today.toEpochDay() - startDate.toEpochDay() + 1//计算相差天数
        textview.text = "今天是" + strMyName + "和" + strLovesName + "在一起的第" + days + "天"//输出
        val path = getExternalFilesDir("background/background.jpg").toString()//图片路径
        if (!TextUtils.isEmpty(path)) {//路径不存在则不加载图片
            val bitmap = BitmapFactory.decodeFile(path)
            try {//判断图片是否正确加载
                bitmap.width
            } catch (e: Exception) {
            }
            val drawable = BitmapDrawable(resources, bitmap)//设置背景图片
            cl.background = drawable
            cl.background.alpha = 50
        }
        switch1.setOnCheckedChangeListener { switch1, isChecked ->//切换天数格式
            if (isChecked) {
                switch1.text = getString(R.string.yy_mm_dd)
                val day = (period.days + 1).toLong()
                if (period.years == 0 && period.months == 0)
                    textview.text = "今天是" + strMyName + "和" + strLovesName + "在一起的第" + day + "天"
                else if (period.years == 0)
                    textview.text = "今天是" + strMyName + "和" + strLovesName + "在一起的第" + period.months + "月" + day + "天"
                else if (period.months == 0)
                    textview.text = "今天是" + strMyName + "和" + strLovesName + "在一起的第" + period.years + "年" + day + "天"
                else
                    textview.text =
                        "今天是" + strMyName + "和" + strLovesName + "在一起的第" + period.years + "年" + period.months + "月" + day + "天"
            } else {
                switch1.text = getString(R.string.days)
                textview.text = "今天是" + strMyName + "和" + strLovesName + "在一起的第" + days + "天"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {//添加菜单
        menu?.add(Menu.NONE, 0, 0, "重新设置")
        menu?.add(Menu.NONE, 1, 1, "选择背景图片")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {//判断打开的是哪个菜单项
            0 -> startActivity(Intent(this, SettingActivity::class.java))//点击重新设置菜单打开设置activity
            1 -> {//点击选择图片打开相册
                val getAlbum = Intent(Intent.ACTION_PICK)
                getAlbum.type = "image/*"
                startActivityForResult(getAlbum, IMAGE_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//获取得到的图片路径并保存
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK)
            return
        if (requestCode == IMAGE_CODE) {
            val originalURI = data!!.data!!
            val backgroundBitmap=BackgroundBitmap(this,originalURI)
            backgroundBitmap.cut()
            val drawable = BitmapDrawable(resources, backgroundBitmap.bitmap)
            cl.background = drawable
            cl.background.alpha = 50
            saveBitmap(backgroundBitmap.bitmap)
        }
    }

    private fun saveBitmap(bitmap: Bitmap) {//保存图片到data/com.yst.Calendar/files/background
        val appDir = File(getExternalFilesDir(null), "background")
        if (!appDir.exists())
            appDir.mkdir()
        val fileName = "background" + ".jpg"
        val file = File(appDir, fileName)
        if (file.exists()) {
            file.delete()
            file.createNewFile()
        }
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
    }
}

