package com.hyn.textimage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.hyn.textimage.fragment.PropertiesBSFragment
import com.hyn.textimage.fragment.TextEditorDialogFragment
import com.luck.picture.lib.entity.LocalMedia
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.PhotoEditor
import android.widget.Toast
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_quick_image.*
import java.io.File
import java.lang.Exception


open class QuickImageActivity : BaseActivity(), View.OnClickListener, PropertiesBSFragment.Properties {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, QuickImageActivity::class.java)
        }
    }

    val editLayout: LinearLayout by lazy { findViewById<LinearLayout>(R.id.edit_layout) }
    val bruch: ImageView by lazy { findViewById<ImageView>(R.id.bruch) }
    val box: ImageView by lazy { findViewById<ImageView>(R.id.box) }
    val font: ImageView by lazy { findViewById<ImageView>(R.id.font) }
    val imgUndo: ImageView by lazy { findViewById<ImageView>(R.id.imgUndo) }
    val imgRedo: ImageView by lazy { findViewById<ImageView>(R.id.imgRedo) }
    val photoEditorView: PhotoEditorView by lazy { findViewById<PhotoEditorView>(R.id.photoEditorView) }
    val photoView: ImageView by lazy { findViewById<ImageView>(R.id.image) }
    lateinit var photoEditor: PhotoEditor
    lateinit var propertyFragment: PropertiesBSFragment
    lateinit var textFragment: TextEditorDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_image)
        createToolbar()

        initEdit()
        bruch.setOnClickListener(this)
        box.setOnClickListener(this)
        font.setOnClickListener(this)
        imgUndo.setOnClickListener(this)
        imgRedo.setOnClickListener(this)
        propertyFragment = PropertiesBSFragment()
        propertyFragment.setPropertiesChangeListener(this)
        photoEditor = PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
//                .setDefaultTextTypeface(mTextRobotoTf)
//                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build()
    }


    open fun initEdit() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)// 最大图片选择数量 int
                .selectionMode(PictureConfig.SINGLE)
                .previewImage(true)
                .isCamera(true)
                .isZoomAnim(true)
                .sizeMultiplier(0.6f)
                .freeStyleCropEnabled(true)
//                .enableCrop(true)// 是否裁剪 true or false
                .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(true)// 是否开启点击声音 true or false
//                .selectionMedia(true)// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .cropCompressQuality(90)// 裁剪压缩质量 默认90 int
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .isDragFrame(true)// 是否可拖动裁剪框(固定)
                .forResult(PictureConfig.CHOOSE_REQUEST)//结果回调onActivityResult code

    }

    fun createToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "图片编辑"
        toolbar.setNavigationOnClickListener {
            finish()
        }
        //左边的小箭头（注意需要在setSupportActionBar(toolbar)之后才有效果）
        //设置toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //参数为菜单资源id
        toolbar.setOnMenuItemClickListener(itemListener)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolmenu, menu)
        return true
    }

    private val itemListener = Toolbar.OnMenuItemClickListener {
        if(it.itemId == R.id.toolbar_save) {
            imageSave(toolbar)
            true
        }
        false
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bruch -> {
                photoEditor.setBrushDrawingMode(true)
                propertyFragment.show(supportFragmentManager, "bruch")
                refrechTab(0)
            }
            R.id.font -> {
                val textEditorDialogFragment = TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener(object : TextEditorDialogFragment.TextEditor {
                    override fun onDone(inputText: String, colorCode: Int) {
                        photoEditor.brushEraser()
                        photoEditor.addText(inputText, colorCode)
                    }
                })
                refrechTab(1)
            }
            R.id.box -> {
                photoEditor.brushEraser()
                refrechTab(2)
            }

            R.id.imgUndo -> {
                photoEditor.undo()
            }

            R.id.imgRedo -> {
                photoEditor.redo()
            }

        }
    }

    @SuppressLint("MissingPermission")
    fun imageSave(v : View) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("确认保存图片？")//提示内容
        builder.setPositiveButton("确定") { dialog, which ->
            val file = File(Environment.getExternalStorageDirectory().toString()
                    + File.separator + "文字图片"
                    + System.currentTimeMillis() + ".png")
            photoEditor.saveAsFile(file.path, object : PhotoEditor.OnSaveListener {
                override fun onSuccess(imagePath: String) {
                    Toast.makeText(v.context, "已成功保存至手机SD卡", Toast.LENGTH_SHORT).show()

                }

                override fun onFailure(exception: Exception) {
                    Toast.makeText(v.context, "保存失败", Toast.LENGTH_SHORT).show()
                }
            })
            dialog.dismiss()
        }
        builder.setNegativeButton("取消") { dialog, which ->
            dialog.cancel()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (photoEditor.brushSize.toInt() == 0) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("图片未保存，确认退出？")//提示内容
            builder.setPositiveButton("确定") { dialog, which ->
                dialog.dismiss()
                finish()
            }
            builder.setNegativeButton("取消") { dialog, which ->
                dialog.cancel()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    if (selectList != null && selectList.isNotEmpty()) {
                        imageEdit(selectList[0])
                    } else {
                        finish()
                    }
                }
            }
        } else {
            finish()
        }
    }


    fun imageEdit(selectMedia: LocalMedia) {

        //Use custom font using latest support library
//        val mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium)

        //loading font from assest
//        val mEmojiTypeFace = Typeface.createFromAsset(assets, "emojione-android.ttf")

        if (selectMedia != null) {
            photoEditorView.post {
                Glide.with(photoEditorView.source).asBitmap().load(if (selectMedia.isCompressed)
                    selectMedia.compressPath else selectMedia.path)
                        .into(object : SimpleTarget<Bitmap>(photoEditorView.width, photoEditorView.height) {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                photoEditorView.source.setImageBitmap(resource)
                            }
                        })
            }
        }
    }

    override fun onColorChanged(colorCode: Int) {
        photoEditor.brushColor = colorCode
    }

    override fun onOpacityChanged(opacity: Int) {
        photoEditor.setOpacity(opacity)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        photoEditor.setBrushSize(brushSize.toFloat())
    }


    private fun refrechTab(pos: Int) {
        when (pos) {
            0 -> {
                bruch.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                box.setBackgroundColor(0)
                font.setBackgroundColor(0)
            }
            1 -> {
                bruch.setBackgroundColor(0)
                font.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                box.setBackgroundColor(0)
            }
            2 -> {
                bruch.setBackgroundColor(0)
                font.setBackgroundColor(0)
                box.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            }
        }
    }
}
