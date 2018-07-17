package com.hyn.textimage
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about.*
import android.content.pm.PackageManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo



class AboutActivity : BaseActivity() {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, AboutActivity::class.java)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        toolbar.title = "关于"
        version.text = "文字图片 v" + getVersionName(context = this) +" 版权所有@CopyRight.2018 "
        toolbar.setNavigationOnClickListener {
            finish()
        }
        //左边的小箭头（注意需要在setSupportActionBar(toolbar)之后才有效果）
        //设置toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    fun getVersionName(context: Context): String {
        val packageManager = context.getPackageManager()
        val packageInfo: PackageInfo
        var versionName = ""
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0)
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return versionName
    }
}
