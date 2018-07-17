package com.hyn.textimage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.hyn.textimage.fragment.PropertiesBSFragment
import com.hyn.textimage.fragment.TextEditorDialogFragment
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.yalantis.ucrop.UCrop
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import kotlinx.android.synthetic.main.activity_quick_image.*
import java.io.File
import java.lang.Exception


open class QuickImageActivity : BaseActivity(), View.OnClickListener, PropertiesBSFragment.Properties {

    companion object {
        val CROP_SMALL_PICTURE = 1

        fun createIntent(context: Context): Intent {
            return Intent(context, QuickImageActivity::class.java)
        }
    }

    var cropFile: String? = null
    val editLayout: LinearLayout by lazy { findViewById<LinearLayout>(R.id.edit_layout) }
    val bruch: LinearLayout by lazy { findViewById<LinearLayout>(R.id.bruch) }
    val box: LinearLayout by lazy { findViewById<LinearLayout>(R.id.box) }
    val font: LinearLayout by lazy { findViewById<LinearLayout>(R.id.font) }
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
        createToolbar(itemListener)

        initEdit()
        bruch.setOnClickListener(this)
        box.setOnClickListener(this)
        font.setOnClickListener(this)
        imgUndo.setOnClickListener(this)
        imgRedo.setOnClickListener(this)
        cutting.setOnClickListener(this)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolmenu, menu)
        return true
    }

    private val itemListener = Toolbar.OnMenuItemClickListener {
        if (it.itemId == R.id.toolbar_save) {
            imageSave(toolbar)
            true
        }
        false
    }


    @SuppressLint("MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bruch -> {
                photoEditor.setBrushDrawingMode(true)
                propertyFragment.setCurrentColor(photoEditor.brushColor)
                propertyFragment.show(supportFragmentManager, "bruch")
                refrechTab(v.id)
            }
            R.id.font -> {
                val textEditorDialogFragment = TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener(object : TextEditorDialogFragment.TextEditor {
                    override fun onDone(inputText: String, colorCode: Int) {
                        photoEditor.brushEraser()
                        photoEditor.addText(inputText, colorCode)
                    }
                })
                refrechTab(v.id)
            }
            R.id.box -> {
                photoEditor.brushEraser()
                refrechTab(v.id)
            }

            R.id.imgUndo -> {
                photoEditor.undo()
            }

            R.id.imgRedo -> {
                photoEditor.redo()
            }

            R.id.cutting -> {
                val file = File(application.externalCacheDir.path
                        + File.separator +
                        +System.currentTimeMillis() + ".png")
                photoEditor.saveAsFile(file.path, object : PhotoEditor.OnSaveListener {
                    override fun onSuccess(imagePath: String) {
                        startCropImage(imagePath, photoEditorView.width, photoEditorView.height)
                    }

                    override fun onFailure(exception: Exception) {
                        Toast.makeText(v.context, "未知错误,无法裁剪图片", Toast.LENGTH_SHORT).show()
                    }
                })
                refrechTab(v.id)
            }

        }
    }

    @SuppressLint("MissingPermission")
    fun imageSave(v: View) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("确认保存图片？")//提示内容
        builder.setPositiveButton("确定") { dialog, which ->
            val childFile = File(ImageUtil.IMAGE_PATH, System.currentTimeMillis().toString() + ".png")
            if(!childFile.exists()) {
                childFile.parentFile.mkdirs()
                childFile.createNewFile()
            }
            photoEditor.saveAsFile(childFile.path, object : PhotoEditor.OnSaveListener {
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

                CROP_SMALL_PICTURE -> {
                    if (data != null) {
                        setImageToView(data) // 让刚才选择裁剪得到的图片显示在界面上
                    }
                }

                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    Glide.with(photoEditorView.source).asBitmap().load(resultUri)
                            .into(object : SimpleTarget<Bitmap>(photoEditorView.width, photoEditorView.height) {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    photoEditorView.source.setImageBitmap(resource)
                                    val file = File(resultUri?.path)
                                    if (file.exists()) {
                                        file.delete()
                                    }
                                }
                            })
                }
            }
        } else {
            if (UCrop.REQUEST_CROP != requestCode) {
                finish()
            } else {
                cropFile?.let {
                    val file = File(cropFile)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }

        }
    }


    fun imageEdit(selectMedia: LocalMedia) {

        //Use custom font using latest support library
//        val mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium)

        //loading font from assest
//        val mEmojiTypeFace = Typeface.createFromAsset(assets, "emojione-android.ttf")
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

    override fun onColorChanged(colorCode: Int) {
        photoEditor.brushColor = colorCode
    }

    override fun onOpacityChanged(opacity: Int) {
        photoEditor.setOpacity(opacity)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        photoEditor.setBrushSize(brushSize.toFloat())
    }


    private fun refrechTab(id: Int) {
        if (id == bruch.id) {
            bruch.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        } else {
            bruch.setBackgroundColor(0)
        }
        if (id == font.id) {
            font.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        } else {
            font.setBackgroundColor(0)
        }

        if (id == box.id) {
            box.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        } else {
            box.setBackgroundColor(0)
        }

        if (id == cutting.id) {
            cutting.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        } else {
            cutting.setBackgroundColor(0)
        }
    }

    /**
     * 保存裁剪之后的图片数据
     */
    protected fun setImageToView(data: Intent) {
        val extras = data.extras
        if (extras != null) {
            val bitmap = extras.getParcelable<Bitmap>("data")
            //这里图片是方形的，可以用一个工具类处理成圆形（很多头像都是圆形，这种工具类网上很多不再详述）
            photoEditorView.source.setImageBitmap(bitmap)
        }
    }


    private fun startCropImage(imagePath: String, width: Int, height: Int) {
        cropFile = imagePath
        UCrop.of(Uri.parse("file://" + imagePath), Uri.parse("file://" + imagePath))
                .withAspectRatio(width.toFloat(), height.toFloat())
                .withMaxResultSize(width, height)
                .start(this)
    }
}
