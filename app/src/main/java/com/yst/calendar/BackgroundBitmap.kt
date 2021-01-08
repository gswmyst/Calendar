package com.yst.calendar

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.zhuolong.bitmaphelper.BitmapHelperFactory
import com.zhuolong.bitmaphelper.shape.BitmapShapeOption
import kotlinx.android.synthetic.main.activity_main.*

class BackgroundBitmap(private val activity: MainActivity, uri: Uri) {
    var bitmap: Bitmap

    init {
        //初始化时对图像进行压缩,再给bitmap赋值
        var input = activity.contentResolver.openInputStream(uri)
        val option = BitmapFactory.Options()
        option.inJustDecodeBounds = true
        BitmapFactory.decodeStream(input, null, option)
        input!!.close()
        val originalWidth = option.outWidth
        val originalHeight = option.outHeight
        val width = activity.cl.width
        val height = activity.cl.height
        option.inJustDecodeBounds = false
        input = activity.contentResolver.openInputStream(uri)
        if (originalWidth > width && originalHeight > height) {
            //如果图片宽高均大于手机分辨率对图片进行压缩
            val ratio: Int = if ((originalHeight / height) > (originalWidth / width)) {
                originalWidth / width
            } else {
                originalHeight / height
            }
            option.inSampleSize = ratio
            bitmap = BitmapFactory.decodeStream(input, null, option)!!
        } else {
            //如果图片宽高有一个小于手机分辨率对图片进行拉伸
            val ratio: Float =
                if ((height.toFloat() / originalHeight.toFloat()) > (width.toFloat() / originalWidth.toFloat())) {
                    height.toFloat() / originalHeight.toFloat()
                } else {
                    width.toFloat() / originalWidth.toFloat()
                }
            val newHeight: Int = (originalHeight * ratio).toInt()
            val newWidth: Int = (originalWidth * ratio).toInt()
            bitmap = BitmapFactory.decodeStream(input, null, option)!!
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }
        input!!.close()

        //获取图片信息，如有旋转处理旋转
        val exifInterface = ExifInterface(activity.contentResolver.openInputStream(uri)!!)
        val mat = Matrix()
        when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> mat.postRotate(90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> mat.postRotate(180F)
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, mat, true)
    }

    fun cut() {
        val rect = Rect(0, 0, activity.cl.width, activity.cl.height)//设置裁剪的长宽
        val option = BitmapShapeOption.Builder().build()
        val helper = BitmapHelperFactory.newBitmapShapeHelper()
        bitmap = helper.bitmapRectShapeHelper.clipRectShapeInCenter(bitmap, rect, option)
    }
}