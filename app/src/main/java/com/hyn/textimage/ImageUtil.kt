package com.hyn.textimage

import android.support.v4.app.NotificationCompat.getExtras
import android.os.Bundle
import android.content.Intent
import android.os.Environment
import android.support.v4.app.ActivityCompat.startActivityForResult



/**
 * Created by huangyanan on 2018/7/16.
 */
class ImageUtil {


    companion object {
        val IMAGE_PATH = Environment.getExternalStorageDirectory().path + "/文字图片"
    }
}